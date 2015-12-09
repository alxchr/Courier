package ru.aldi_service.courier;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alx on 06.11.15.
 */
public class PGutils {
    final static String LOG_TAG = "PostgreSQL";

    public static Connection dbConnect(String databaseUrl, String databaseUser, String databasePassword) {
        Connection connection;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "No PostgreSQL JDBC Driver");
            e.printStackTrace();
            return null;
        }
        try {
            connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        } catch (SQLException e) {
            Log.d(LOG_TAG, "Connection Failed!");
            e.printStackTrace();
            return null;
        }
        return connection;
    }
}
