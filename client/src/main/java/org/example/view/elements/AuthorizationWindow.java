package org.example.view.elements;

import org.example.view.listeners.NicknameEnteredListener;

import javax.swing.*;
import java.awt.*;

public class AuthorizationWindow extends JFrame {
    private final GridBagLayout mainLayout;
    private NicknameEnteredListener nicknameEnteredListener;
    private JTextArea nameArea;

    public AuthorizationWindow() {
        super("Enter your nickname");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainLayout = new GridBagLayout();
        Container contentPane = getContentPane();
        contentPane.setLayout(mainLayout);
        contentPane.setBackground(new Color(50, 67, 230));
        setResizable(false);
        setSize(500, 300);

        setupWindows();
        setupLabels();
        setupButton();
    }

    public void resetText() {
        nameArea.setText("");
    }

    private void setupWindows() {
        GridBagConstraints gcon = new GridBagConstraints();

        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.fill = GridBagConstraints.HORIZONTAL;
        gcon.anchor = GridBagConstraints.NORTH;
        gcon.insets = new Insets(15, 15, 15, 15);

        gcon.gridx = 0;
        gcon.gridy = 1;

        nameArea = new JTextArea(2, 10);
        nameArea.setFont(new Font("Serif", Font.PLAIN, 25));
        setUpTextArea(nameArea);
        mainLayout.setConstraints(nameArea, gcon);
        add(nameArea);
    }

    private void setupLabels() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.fill = GridBagConstraints.CENTER;
        gcon.anchor = GridBagConstraints.SOUTH;
        gcon.insets = new Insets(15, 15, 15, 15);

        JLabel urlLabel = new JLabel("Enter Unique Nickname");
        urlLabel.setForeground(Color.WHITE);
        urlLabel.setFont(new Font("Serif", Font.BOLD, 25));
        gcon.gridx = 0;
        gcon.gridy = 0;
        gcon.gridwidth = 1;
        gcon.gridheight = 1;
        mainLayout.setConstraints(urlLabel, gcon);
        add(urlLabel);

    }

    private void setupButton() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.fill = GridBagConstraints.HORIZONTAL;
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.insets = new Insets(15, 15, 15, 15);
        JButton sendNameButton = new JButton("Enter Nickname");
        sendNameButton.setFont(new Font("Serif", Font.PLAIN, 18));
        sendNameButton.addActionListener(e -> {
            String name = nameArea.getText();
            if (!name.isBlank()) {
                nicknameEnteredListener.onNicknameEntered(name);
            }
        });

        gcon.gridx = 0;
        gcon.gridy = 2;
        gcon.gridwidth = 1;
        gcon.gridheight = 1;

        sendNameButton.setSize(new Dimension(50, 50));
        mainLayout.setConstraints(sendNameButton, gcon);
        add(sendNameButton);
    }

    private void setUpTextArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("");
    }

    public void setAuthorizationRequestListener(NicknameEnteredListener nicknameEnteredListener) {
        this.nicknameEnteredListener = nicknameEnteredListener;
    }
}
