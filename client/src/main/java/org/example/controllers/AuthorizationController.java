package org.example.controllers;

import org.example.model.ClientManager;
import org.example.view.listeners.NicknameEnteredListener;

public class AuthorizationController implements NicknameEnteredListener {

    private final ClientManager clientManager;

    public AuthorizationController(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void onNicknameEntered(String nickname) {
        clientManager.setClientNickname(nickname);
    }
}
