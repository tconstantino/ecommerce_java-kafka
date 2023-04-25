package org.example;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {
    OrdersDatabase() throws SQLException {
        this.database = new LocalDatabase("orders_database");
        // You might want to save all data
        this.database.createIfNotExists("CREATE TABLE ORDERS (UUID VARCHAR(200) PRIMARY KEY)");
    }

    private final LocalDatabase database;

    public Boolean isNewOrder(Order order) throws SQLException {
        var result = database.query("SELECT UUID FROM ORDERS WHERE UUID = ? LIMIT 1", order.getOrderId());
        return !result.next();
    }

    public void insertOrder(Order order) throws SQLException {
        database.insertOrUpdate("INSERT INTO ORDERS (UUID) VALUES (?)", order.getOrderId());
    }

    @Override
    public void close() throws IOException {
        try {
            database.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
