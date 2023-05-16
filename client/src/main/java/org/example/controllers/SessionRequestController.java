package org.example.controllers;

import org.example.model.ClientManager;
import org.example.view.listeners.SessionRequestListener;

public class SessionRequestController implements SessionRequestListener {

    private final ClientManager clientManager;

    public SessionRequestController(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void onSessionStartRequested(String URL, int port) {
        clientManager.startClientSession(URL, port);
    }
}
