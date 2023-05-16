package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.io.ConfigurationReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// SERVER
public class Server {

    private static final Logger LOGGER = LogManager.getLogger(Server.class);

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void requestServerStart(String configurationFile) {
        int port = ConfigurationReader.readPortOrDefault(configurationFile);
        if (isAvailable(port)) {
            LOGGER.info("Server started. Server port: {}", port);
            startServer(port);
        } else {
            LOGGER.warn("Port {} is not available at the given time", port);
        }
    }

    private static void startServer(int port) {
        ServerSocket ss;
        try {
            ss = new ServerSocket(port);
            Server server = new Server(ss);
            server.runServer();
        } catch (IOException e) {
            LOGGER.warn("Error occurred while opening server socket: {}", e.getMessage());
        }
    }

    private static boolean isAvailable(int portNr) {
        boolean portFree;
        try (var ignored = new ServerSocket(portNr)) {
            portFree = true;
        } catch (IOException e) {
            portFree = false;
        }
        return portFree;
    }

    private void runServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            LOGGER.warn("Error occurred while waiting for new connection: {}", e.getMessage());
        }
    }
}