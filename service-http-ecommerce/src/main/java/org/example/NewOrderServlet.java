package org.example;
import org.example.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
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
            var key = userEmail;
            var orderCorrelationId = new CorrelationId(NewOrderServlet.class.getSimpleName());
            var orderId = UUID.randomUUID().toString();
            var order = new Order(orderId, amount, userEmail);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, orderCorrelationId, order);
            System.out.println("New order sent successfully");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent! id: " + orderId);
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
