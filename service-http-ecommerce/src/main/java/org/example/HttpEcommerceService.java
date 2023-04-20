package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {
    public static void main(String[] args) throws Exception {
        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServlet()), "/order/new");
        context.addServlet(new ServletHolder(new GenerateAllReportsServlet()), "/admin/generate/reports");

        var server = new Server(8080);
        server.setHandler(context);
        server.start();
        System.out.println("HTTP Server is running on port 8080");
        server.join();
    }
}
