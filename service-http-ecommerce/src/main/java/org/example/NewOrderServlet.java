package org.example;
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
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // We are not caring about any security issues
            // We are only showing hot to user http as a starting point
            var userEmail = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));
            var key = userEmail;
            var orderId = UUID.randomUUID().toString();
            var order = new Order(orderId, amount, userEmail);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", key, order);

            var subject = "New order";
            var body = "Thank you for your order! We are processing your order!";
            var emailCode = new Email(subject, body);
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", key, emailCode);

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
