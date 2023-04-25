package org.example;
import org.example.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // We are not caring about any security issues
            // We are only showing hot to user http as a starting point
            var userEmail = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));
            var orderId = req.getParameter("uuid");

            var key = userEmail;
            var orderCorrelationId = new CorrelationId(NewOrderServlet.class.getSimpleName());
            var order = new Order(orderId, amount, userEmail);


            try(var ordersDatabase = new OrdersDatabase()) {
                if(ordersDatabase.isNewOrder(order)) {
                    ordersDatabase.insertOrder(order);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, orderCorrelationId, order);
                    System.out.println("New order " + orderId + " sent successfully");

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("New order sent! id: " + orderId);
                } else {
                    System.out.println("Order " + orderId + " was already sent");
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().println("Order was already sent! id: " + orderId);
                }
            }
        } catch (ExecutionException | InterruptedException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
