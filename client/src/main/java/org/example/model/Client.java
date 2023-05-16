package org.example.model;

import lombok.extern.log4j.Log4j2;
import org.example.JsonMessageConverter;
import org.example.Message;
import org.example.MessageType;
import org.example.model.listeners.AuthorizationRequestListener;
import org.example.model.listeners.ChatListListener;
import org.example.model.listeners.MessageReceivedListener;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Log4j2
public class Client {
    private final CyclicBarrier serverAuthorizationBarrier = new CyclicBarrier(2);
    private final CyclicBarrier viewInputAuthorizationBarrier = new CyclicBarrier(2);
    private final CyclicBarrier viewInputMessageBarrier = new CyclicBarrier(2);

    private final Socket socket;
    private final BufferedReader console;
    private final AuthorizationRequestListener authorizationRequestListener;
    private final MessageReceivedListener messageReceivedListener;
    private final ChatListListener chatListListener;

    private BufferedReader reader;
    private PrintWriter writer;
    private volatile String nickname = "";
    private volatile String inputMessage = "";
    private boolean isAuthorized;

    private Client(Socket socket, ChatListListener chatListListener, MessageReceivedListener messageReceivedListener,
                   AuthorizationRequestListener authorizationRequestListener) {
        this.socket = socket;
        console = new BufferedReader(new InputStreamReader(System.in));
        isAuthorized = false;
        this.chatListListener = chatListListener;
        this.messageReceivedListener = messageReceivedListener;
        this.authorizationRequestListener = authorizationRequestListener;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            log.warn(e);
            closeConnection();
        }
    }

    public static Client createClient(String URL, int port, ChatListListener chatListListener,
                                      MessageReceivedListener messageReceivedListener,
                                      AuthorizationRequestListener authorizationRequestListener) {
        Client client = null;
        try {
            Socket socket = new Socket(URL, port);
            client = new Client(socket, chatListListener, messageReceivedListener, authorizationRequestListener);
        } catch (IOException e) {
            log.warn("Invalid URL or PORT: " + e.getMessage());
        }

        return client;
    }

    public void startClientSession() {
        new Thread(() -> {
            try {
                startListeningThread();
                while (!isAuthorized) {
                    tryAuthorize();
                }
                startWriting();
            } catch (IOException | BrokenBarrierException | InterruptedException e) {
                closeConnection();
                log.warn(e);
            }
        }).start();
    }

    private void startWriting() throws BrokenBarrierException, InterruptedException {
        try {
            while (socket.isConnected()) {
                viewInputMessageBarrier.await();
                writeMessageToSocket(new Message(MessageType.CHAT_TEXT, inputMessage));
            }
        } catch (IOException e) {
            log.warn(e);
            closeConnection();
        }
    }

    private void tryAuthorize() throws IOException, BrokenBarrierException, InterruptedException {
        if (socket.isConnected()) {
            authorizationRequestListener.onAuthorizationUpdate(true);
            viewInputAuthorizationBarrier.await();

            // К этому моменту сработает setter от контроллера, который продвинет счетчик барьера барьер.
            Message authMsg = new Message(MessageType.AUTHORIZATION_REQUEST, nickname);
            writeMessageToSocket(authMsg);
            serverAuthorizationBarrier.await();  // Ждем, пока не придет ответ от сервера
        }
    }

    private void startListeningThread() {
        new Thread(() -> {
            try {
                startListening();
            } catch (BrokenBarrierException | InterruptedException | IOException e) {
                log.warn(e);
                closeConnection();
            }
        }).start();
    }

    private void startListening() throws BrokenBarrierException, InterruptedException, IOException {
        String jsonString;
        while (socket.isConnected() && (jsonString = reader.readLine()) != null) {
            Message msg = JsonMessageConverter.jsonToMessage(jsonString);
            processMessage(msg);
        }
    }

    private void processMessage(Message msg) throws BrokenBarrierException,
            InterruptedException {
        switch (msg.getMessageType()) {
            case AUTHORIZATION_APPROVED -> {
                isAuthorized = true;
                serverAuthorizationBarrier.await();
                authorizationRequestListener.onAuthorizationUpdate(false);
            }
            case AUTHORIZATION_DENIED -> {
                serverAuthorizationBarrier.await();
            }
            case CHAT_TEXT -> {
                String msgWithTime = MessageTimeFormatter.addTimeToMessage(msg.getContent());
                messageReceivedListener.onChatMessageReceived(msgWithTime);
            }
            case UPDATE_USER_LIST -> {
                chatListListener.onChatListChanged(msg.getContent());
            }
        }
    }

    public void setAuthorizationNickname(String lineFromInput) {
        nickname = lineFromInput;
        try {
            viewInputAuthorizationBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            log.warn(e);
        }
    }

    public void setInputMessage(String message) {
        inputMessage = message;
        try {
            viewInputMessageBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            log.warn(e);
        }
    }

    private void writeJsonToSocket(String jsonString) {
        writer.println(jsonString);
        writer.flush();
    }

    private void writeMessageToSocket(Message message) throws IOException {
        writeJsonToSocket(JsonMessageConverter.messageToJson(message));
    }

    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (console != null) {
                console.close();
            }
        } catch (IOException e) {
            log.warn("Problem occurred while closing connection: {}", e.getMessage());
        }
    }
}
