package org.example;
import org.example.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {
    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var key = UUID.randomUUID().toString();
            var correlationId = new CorrelationId(GenerateAllReportsServlet.class.getSimpleName());
            var value = "ECOMMERCE_USER_GENERATE_READING_REPORT";
            batchDispatcher.send("ECOMMERCE_SEND_MESSAGE_TO_ALL_USES", key, correlationId, value);

            System.out.println("Sent generate reports to all users");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Report requests generated: " + new Date().toString());
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
