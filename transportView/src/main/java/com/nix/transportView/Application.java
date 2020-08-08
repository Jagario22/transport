package com.nix.transportView;

import com.nix.transportView.builder.GraphBuilder;
import com.nix.weightedGraph.GraphWeighted;
import com.nix.transportModels.models.*;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Application {
    private static List<Long> locationIds = new ArrayList<>();
    private static List<Route> routes = new ArrayList<>();
    private static List<Problem> problems = new ArrayList<>();
    private static List<Solution> solutions = new ArrayList<>();
    private static Connection connection;

    public static void main(String[] args) {
        int cost;
        GraphWeighted graph;
        Properties props = loadProperties();
        String url = props.getProperty("url");
        log.info("Connecting to {}", url);

        try {
            connection = DriverManager.getConnection(url, props);
            getDataFromDataBase();
            graph = GraphBuilder.build(locationIds, routes);
            if (problems.size() == 0) {
                log.info("there are no problems to solve");
                return;
            }

            int id = 1;
            for (Problem problem : problems) {
                cost = graph.findCostOfShortestPath(String.valueOf(problem.getFrom_id()), String.valueOf(problem.getTo_id()));
                solutions.add(new Solution(id++, cost));
            }

            insertSolution();
            String[][] locationNames = getLocationNamesFromDataBase();
            for (int i = 0; i < solutions.size(); i++) {
                System.out.println("Solution for path " + locationNames[i][0] + "-" + locationNames[i][1] + ": " + solutions.get(i).getCost());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
                log.info("Connection " + url + " is closed");
            } catch (SQLException e) {
            }
        }

    }

    private static void insertSolution() {
        try (PreparedStatement insertSolution = connection.prepareStatement(
                "INSERT INTO solutions (problem_id, cost) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            for (Solution solution : solutions) {
                insertSolution.setLong(1, solution.getProblem_id());
                insertSolution.setInt(2, solution.getCost());

                insertSolution.addBatch();
            }

            insertSolution.executeBatch();
            ResultSet generatedKeys = insertSolution.getGeneratedKeys();

            while (generatedKeys.next()) {
                log.info("inserted new solution: id: {}", generatedKeys.getLong("problem_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = Application.class.getResourceAsStream("jdbs.properties")) {
            props.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return props;
    }

    private static String[][] getLocationNamesFromDataBase() {
        String[][] locationNames = new String[problems.size()][2];
        try (PreparedStatement getLocationNames = connection.prepareStatement(
                "select name from locations l where l.id = ? OR l.id = ?"
        )) {
            int i = 0;
            int j = 0;

            ResultSet resultSet;
            for (Problem problem : problems) {
                getLocationNames.setLong(1, problem.getFrom_id());
                getLocationNames.setLong(2, problem.getTo_id());

                resultSet = getLocationNames.executeQuery();

                while (resultSet.next()) {
                    locationNames[i][j++] = resultSet.getString("name");
                }
                i++;
                j = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locationNames;
    }

    private static void getDataFromDataBase() {
        try (Statement getData = connection.createStatement()) {
            ResultSet resultSet = getData.executeQuery("SELECT * from locations");
            long locationId;
            while (resultSet.next()) {
                locationId = resultSet.getLong("id");
                locationIds.add(locationId);
                log.debug("adding location: " + locationId);
            }

            resultSet = getData.executeQuery("SELECT * from routes");
            Route route;
            while (resultSet.next()) {
                route = new Route(resultSet.getLong("id"), resultSet.getLong("from_id"), resultSet.getLong("to_id"), resultSet.getInt("cost"));
                routes.add(route);
                log.debug("adding route: " + route.getId() + ", " + route.getFrom_id() + ", " + route.getTo_id() + ", " + route.getCost());
            }

            resultSet = getData.executeQuery("select id, from_id, to_id from problems p " +
                    "left join solutions s on p.id = s.problem_id where s.problem_id is null");

            Problem problem;
            while (resultSet.next()) {
                problem = new Problem(resultSet.getLong("id"), resultSet.getLong("from_id"), resultSet.getLong("to_id"));
                problems.add(problem);
                log.debug("adding problem: " + problem.getId() + ", " + problem.getFrom_id() + ", " + problem.getTo_id());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
