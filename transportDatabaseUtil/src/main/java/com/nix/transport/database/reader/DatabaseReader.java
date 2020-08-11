package com.nix.transport.database.reader;


import com.nix.transport.database.models.Problem;
import com.nix.transport.database.models.Route;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DatabaseReader {
    private  Connection connection;
    public DatabaseReader(Connection connection) {
        this.connection = connection;
    }

    public List<Long> readLocationIds() {
        final List<Long> locationIds = new ArrayList<>();
        try (Statement getLocations = connection.createStatement()) {
            ResultSet resultSet = getLocations.executeQuery("SELECT * from locations");
            long locationId;
            while (resultSet.next()) {
                locationId = resultSet.getLong("id");
                locationIds.add(locationId);
                log.debug("adding location: " + locationId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return locationIds;
    }

    public List<Route> readRoutes() {
        final List<Route> routes = new ArrayList<>();
        try (Statement getRoutes = connection.createStatement()) {
            ResultSet resultSet = getRoutes.executeQuery("SELECT * from routes");
            Route route;
            while (resultSet.next()) {
                route = new Route(resultSet.getLong("id"), resultSet.getLong("from_id"), resultSet.getLong("to_id"), resultSet.getInt("cost"));
                routes.add(route);
                log.debug("adding route: " + route.getId() + ", " + route.getFrom_id() + ", " + route.getTo_id() + ", " + route.getCost());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public  List<Problem> readProblems() {
        final List<Problem> problems = new ArrayList<>();
        try (Statement getProblems = connection.createStatement()) {
            ResultSet resultSet = getProblems.executeQuery("select id, from_id, to_id from problems p " +
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
        return problems;
    }

    public String[][] getLocationNamesForProblemPaths(List<Problem> problems) {
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

}
