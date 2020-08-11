package com.nix.transport.transportView;

import com.nix.transport.database.connection.ConnectionUtil;
import com.nix.transport.database.models.*;
import com.nix.transport.database.reader.DatabaseReader;
import com.nix.transport.database.writer.DatabaseWriter;
import com.nix.transport.transportView.builder.GraphBuilder;
import com.nix.weightedGraph.GraphWeighted;
import lombok.extern.slf4j.Slf4j;



import java.sql.*;
import java.util.List;

@Slf4j
public class Application {

    private static Connection connection;

    public static void main(String[] args) {
        final List<Long> locationIds;
        final List<Route> routes;
        final List<Problem> problems;
        final List<Solution> solutions;
        final DatabaseReader databaseReader;
        final DatabaseWriter databaseWriter;
        final Solver solver = new Solver();
        GraphWeighted graph;

        try {
            connection = ConnectionUtil.connect("jdbs.properties");
            databaseReader = new DatabaseReader(connection);
            databaseWriter = new DatabaseWriter(connection);

            locationIds = databaseReader.readLocationIds();
            routes = databaseReader.readRoutes();
            problems = databaseReader.readProblems();

            graph = GraphBuilder.build(locationIds, routes);

            solutions = solver.getSolutions(problems, graph);
            if (solutions != null) {
                databaseWriter.insertSolutions(solutions);

                String[][] locationNames = databaseReader.getLocationNamesForProblemPaths(problems);
                for (int i = 0; i < solutions.size(); i++) {
                    System.out.println("Solution for path " + locationNames[i][0] + "-" + locationNames[i][1] + ": " + solutions.get(i).getCost());
                }
            }
        } finally {
            ConnectionUtil.closeConnection();
            log.info("Connection is closed");
        }

    }
}
