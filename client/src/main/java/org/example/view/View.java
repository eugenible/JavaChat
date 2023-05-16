package org.example.view;

import org.example.model.listeners.AuthorizationRequestListener;
import org.example.model.listeners.ChatListListener;
import org.example.model.listeners.MessageReceivedListener;
import org.example.model.listeners.SessionResponseListener;
import org.example.view.elements.AuthorizationWindow;
import org.example.view.elements.MainWindow;
import org.example.view.elements.SessionRequestWindow;
import org.example.view.listeners.MessageInputListener;
import org.example.view.listeners.NicknameEnteredListener;
import org.example.view.listeners.SessionRequestListener;

public class View implements AuthorizationRequestListener, ChatListListener, MessageReceivedListener,
        SessionResponseListener {
    private final MessageInputListener messageInputListener;
    private final NicknameEnteredListener nicknameEnteredListener;
    private final SessionRequestListener sessionRequestListener;

    private MainWindow mainWindow;
    private SessionRequestWindow sessionRequestWindow;
    private AuthorizationWindow authorizationWindow;

    public View(MessageInputListener messageInputListener, NicknameEnteredListener nicknameEnteredListener,
                SessionRequestListener sessionRequestListener) {
        this.messageInputListener = messageInputListener;
        this.nicknameEnteredListener = nicknameEnteredListener;
        this.sessionRequestListener = sessionRequestListener;
    }


    public void startGUI() {
        setupGUI();
        sessionRequestWindow.setVisible(true);
    }


    public void setupGUI() {
        mainWindow = new MainWindow();
        mainWindow.setInputListener(messageInputListener);

        authorizationWindow = new AuthorizationWindow();
        authorizationWindow.setAuthorizationRequestListener(nicknameEnteredListener);

        sessionRequestWindow = new SessionRequestWindow();
        sessionRequestWindow.setSessionRequestListener(sessionRequestListener);
    }

//    @Override
//    public void onAuthorizationResponseReceived(boolean isSuccessful) {
//        if (isSuccessful) {
//            authorizationWindow.dispose();
//            mainWindow.setVisible(true);
//        } else {
//            // перезапросить имя
//            authorizationWindow.resetText();
//            authorizationWindow.setVisible(true);
//        }
//    }

    @Override
    public void onChatListChanged(String newChatList) {
        mainWindow.setUserList(newChatList);
    }

    @Override
    public void onChatMessageReceived(String message) {
        mainWindow.appendChatMessage(message);
    }

    @Override
    public void onSessionResponseReceived(boolean isSuccessful) {
        if (isSuccessful) {
            sessionRequestWindow.dispose();
            authorizationWindow.setVisible(true);
        } else {
            // Обновить форму ввода хоста
            sessionRequestWindow.resetText();
            sessionRequestWindow.setVisible(true);
        }
    }

    @Override
    public void onAuthorizationUpdate(boolean needsAuthorization) {
        if (needsAuthorization) {
            authorizationWindow.resetText();
            authorizationWindow.setVisible(true);
        } else {
            authorizationWindow.dispose();
            mainWindow.setVisible(true);
        }
    }
}
