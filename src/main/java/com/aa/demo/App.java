
package com.aa.demo;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8081")
        );

        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", port), 0
        );

        server.createContext("/", exchange -> {
            String response = """
                Java Docker Demo is running!
                Built and deployed using Jenkins.
                Application: java-docker-demo
                """;

            byte[] body = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                .set("Content-Type", "text/plain; charset=UTF-8");

            exchange.sendResponseHeaders(200, body.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(body);
            }
        });

        server.start();
        System.out.println("Java application started on port " + port);
    }
}

