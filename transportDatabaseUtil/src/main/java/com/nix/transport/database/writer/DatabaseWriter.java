package com.nix.transport.database.writer;

import com.nix.transport.database.models.Solution;
import com.nix.transport.database.reader.DatabaseReader;
import lombok.extern.slf4j.Slf4j;

import javax.xml.crypto.Data;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class DatabaseWriter {
    final Connection connection;

    public DatabaseWriter(Connection connection) {
        this.connection = connection;
    }

    public void insertSolutions(List<Solution> solutions) {
        try (PreparedStatement insertSolution = connection.prepareStatement(
                "INSERT INTO solutions (problem_id, cost) VALUES (?, ?)"
        )) {
            for (Solution solution : solutions) {
                insertSolution.setLong(1, solution.getProblem_id());
                insertSolution.setInt(2, solution.getCost());

                insertSolution.addBatch();
            }

            insertSolution.executeBatch();
            for (Solution solution : solutions) {
                log.info("inserted solutions for problem: " + solution.getProblem_id() + ": " + solution.getCost());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
