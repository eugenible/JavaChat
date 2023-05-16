package org.example.model;

import lombok.Setter;
import org.example.model.listeners.AuthorizationRequestListener;
import org.example.model.listeners.ChatListListener;
import org.example.model.listeners.MessageReceivedListener;
import org.example.model.listeners.SessionResponseListener;

public class ClientManager {
    @Setter
    SessionResponseListener sessionResponseListener;

    @Setter
    ChatListListener chatListListener;

    @Setter
    MessageReceivedListener messageReceivedListener;

    @Setter
    AuthorizationRequestListener authorizationRequestListener;

    private Client client;

    public void startClientSession(String URL, int port) {
        if (client == null && port <= 65535) {
            client = Client.createClient(URL, port, chatListListener, messageReceivedListener, authorizationRequestListener);
        }

        if (client != null) {
            client.startClientSession();
            notifySessionResponseListener(true);
        } else {
            notifySessionResponseListener(false);
        }
    }

    public void setClientNickname(String nickname) {
        if (client != null) client.setAuthorizationNickname(nickname);
    }

    public void sendInputMessage(String message) {
        if (client != null && !message.isBlank()) client.setInputMessage(message);
    }

    private void notifySessionResponseListener(boolean isSuccessful) {
        sessionResponseListener.onSessionResponseReceived(isSuccessful);
    }
}
