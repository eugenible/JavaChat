package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(ClientHandler.class);
    private static final List<ClientHandler> CLIENTS = new CopyOnWriteArrayList<>();
    private static final Set<String> NICKNAMES = new LinkedHashSet<>();
    private final Socket socket;
    private String clientName;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            LOGGER.warn("IOException occurred while trying to open socket IO stream", e);
            closeConnection();
        }
    }

    @Override
    public void run() {
        try {
            startListening();
        } catch (IOException e) {
            LOGGER.warn("Connection closed: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void startListening() throws IOException {
        String jsonString;
        while (socket.isConnected() && (jsonString = reader.readLine()) != null) {
            Message message = JsonMessageConverter.jsonToMessage(jsonString);
            processMessage(message);
        }
    }

    private void processMessage(Message message) throws IOException {
        String content = message.getContent();
        switch (message.getMessageType()) {
            case AUTHORIZATION_REQUEST -> {
                if (isNicknameUnique(content)) {
                    clientName = content;
                    CLIENTS.add(this);
                    NICKNAMES.add(clientName);
                    writeMessageToSocket(new Message(MessageType.AUTHORIZATION_APPROVED, ""));
                    broadcastUserListChange();
                    informClientsOnUserChange(clientName, UserChange.CONNECTED);
                } else {
                    String jsonResponse = JsonMessageConverter.messageToJson(
                            new Message(MessageType.AUTHORIZATION_DENIED, "Nickname is not unique"));
                    writeJsonToSocket(jsonResponse);
                }
            }

            case CHAT_TEXT -> {
                String processedText = clientName + ": " + message.getContent();
                sendMessageToClients(new Message(MessageType.CHAT_TEXT, processedText));
            }
        }
    }

    private void informClientsOnUserChange(String clientName, UserChange changeType) throws IOException {
        String action = changeType == UserChange.CONNECTED ? " connected to " : " disconnected from ";
        String msg = "Server: User " + clientName + action + "chat";
        sendMessageToClients(new Message(MessageType.CHAT_TEXT, msg));
    }

    private void broadcastUserListChange() throws IOException {
        sendMessageToClients(new Message(MessageType.UPDATE_USER_LIST, buildStringUserList()));
    }

    private String buildStringUserList() {
        StringBuilder sb = new StringBuilder();
        for (String name : NICKNAMES) {
            sb.append(name).append("\n");
        }
        return sb.toString();
    }

    // synchronized?
    private boolean isNicknameUnique(String nickname) {
        for (ClientHandler client : CLIENTS) {
            if (this == client) {
                continue;
            }

            if (client.clientName.equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    private void removeClientHandler() throws IOException {
        CLIENTS.remove(this);
        if (clientName != null) NICKNAMES.remove(clientName);
        broadcastUserListChange();
        informClientsOnUserChange(clientName, UserChange.DISCONNECTED);
    }

    private void closeConnection() {
        try {
            removeClientHandler(); // Удалить из списка и оповестить участников чата
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            LOGGER.warn("Problem occurred while closing connection", e);
        }
    }

    private void sendMessageToClients(Message message) throws IOException {
        String jsonString = JsonMessageConverter.messageToJson(message);
        for (ClientHandler client : CLIENTS) {
            client.writeJsonToSocket(jsonString);
        }
    }

    private void writeJsonToSocket(String jsonString) {
        writer.println(jsonString);
        writer.flush();
    }

    private void writeMessageToSocket(Message message) throws IOException {
        String jsonString = JsonMessageConverter.messageToJson(message);
        writeJsonToSocket(jsonString);
    }

    private enum UserChange {
        CONNECTED, DISCONNECTED
    }
}


