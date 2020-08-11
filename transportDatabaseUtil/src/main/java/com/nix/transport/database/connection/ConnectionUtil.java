package com.nix.transport.database.connection;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionUtil {
    private static Connection connection;
    private static String path;

    public static Connection connect(String path) {
        ConnectionUtil.path = path;
        Properties props = loadProperties();
        String url = props.getProperty("url");
        log.info("Connecting to {}", url);
        try {
            connection = DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = ConnectionUtil.class.getResourceAsStream(ConnectionUtil.path)) {
            props.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return props;
    }
}
