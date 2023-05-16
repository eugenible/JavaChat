package org.example;

import org.example.controllers.AuthorizationController;
import org.example.controllers.InputController;
import org.example.controllers.SessionRequestController;
import org.example.model.ClientManager;
import org.example.view.View;

public class Main {
    public static void main(String[] args) {

        ClientManager manager = new ClientManager();

        AuthorizationController authorizationController = new AuthorizationController(manager);
        InputController inputController = new InputController(manager);
        SessionRequestController sessionRequestController = new SessionRequestController(manager);

        View view = new View(inputController, authorizationController, sessionRequestController);

        manager.setSessionResponseListener(view);
        manager.setMessageReceivedListener(view);
        manager.setChatListListener(view);
        manager.setAuthorizationRequestListener(view);

        view.startGUI();
    }
}
