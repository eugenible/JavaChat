package org.example.controllers;


import org.example.model.ClientManager;
import org.example.view.listeners.MessageInputListener;

public class InputController implements MessageInputListener {

    private final ClientManager clientManager;

    public InputController(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void onSendButtonClicked(String message) {
        clientManager.sendInputMessage(message);
    }
}
