package org.example.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


public class ConfigurationReader {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationReader.class);

    private static final int DEFAULT_PORT = 2345;

    public static int readPortOrDefault(String configurationFile) {
        Path configPath = Paths.get(configurationFile);
        if (!Files.isReadable(configPath)) {
            LOGGER.warn("Configuration file {} couldn't be opened, using default which is {}", configurationFile,
                    DEFAULT_PORT);
            return DEFAULT_PORT;
        }

        try {
            int port = tryReadPortFromFile(configPath);
            if (!isValidPortNumber(port)) {
                LOGGER.warn("Port {} is invalid port number, using default which is {}", port, DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
            return port;

        } catch (IOException | InvalidInputException e) {
            LOGGER.warn(e.getMessage());
            LOGGER.warn("Using default port number which is {}", DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private static boolean isValidPortNumber(int port) {
        return port >= 0 && port <= 65535;
    }

    private static int tryReadPortFromFile(Path configPath) throws IOException, InvalidInputException {
        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            props.load(reader);
            String strPort = props.getProperty("port");
            if (strPort == null) {
                throw new InvalidInputException("No port number in configuration file");
            }

            String integerPattern = "[1-9]\\d{2,4}$";

            if (!strPort.matches(integerPattern)) {
                throw new InvalidInputException("Provided invalid port number: " + strPort);
            }

            return Integer.parseInt(strPort);
        }
    }
}
