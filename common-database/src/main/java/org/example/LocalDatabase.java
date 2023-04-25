package org.example;

import java.sql.*;
import java.util.UUID;

public class LocalDatabase {
    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:service-users/target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    private final Connection connection;

    // Yes, this is way too generic
    // According to your database tool, avoid injection
    public void createIfNotExists(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            // Be careful, the sql could be wrong, be really careful
            e.printStackTrace();
        }
    }

    private PreparedStatement getPreparedStatement(String statement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < params.length; i++){
            preparedStatement.setString(i+1, params[i]);
        }
        return preparedStatement;
    }

    public void insertOrUpdate(String sql, String ...params) throws SQLException {
        var insertOrUpdate = getPreparedStatement(sql, params);
        insertOrUpdate.execute();
    }

    public ResultSet query(String sql, String ...params) throws SQLException {
        var query = getPreparedStatement(sql, params);
        return query.executeQuery();
    }
}
