package org.example.view.elements;


import org.example.view.listeners.MessageInputListener;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private final GridBagLayout mainLayout;
    private JScrollPane chatScroll;
    private JTextArea chatArea;
    private JTextArea userList;
    private JTextArea chatInput;
    private MessageInputListener inputListener;

    public MainWindow() {
        super("Giga Mega Chat For Real OGs");
        mainLayout = new GridBagLayout();
        Container contentPane = getContentPane();
        contentPane.setLayout(mainLayout);
        contentPane.setBackground(new Color(50, 67, 230));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(900, 610);
        setupLabels();
        setupWindows();
        setupButton();
    }

    public void appendChatMessage(String incomingMessage) {
        chatArea.append(incomingMessage + "\n");
        JScrollBar vertical = chatScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void setUserList(String userList) {
        this.userList.setText(userList);
    }

    public void setInputListener(MessageInputListener inputListener) {
        this.inputListener = inputListener;
    }

    private void setupLabels() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.fill = GridBagConstraints.CENTER;
        gcon.anchor = GridBagConstraints.SOUTH;
        gcon.insets = new Insets(15, 15, 15, 15);

        JLabel chatLabel = new JLabel("Chatting Room");
        chatLabel.setForeground(Color.WHITE);
        chatLabel.setFont(new Font("Serif", Font.BOLD, 25));
        gcon.gridx = 0;
        gcon.gridy = 0;
        gcon.gridwidth = 1;
        gcon.gridheight = 1;
        mainLayout.setConstraints(chatLabel, gcon);
        add(chatLabel);

        JLabel userListLabel = new JLabel("User List");
        userListLabel.setForeground(Color.WHITE);
        userListLabel.setFont(new Font("Serif", Font.BOLD, 25));
        gcon.gridx = 1;
        mainLayout.setConstraints(userListLabel, gcon);
        add(userListLabel);
    }

    private void resetInput() {
        if (chatInput != null) chatInput.setText("");
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
        gcon.gridwidth = 1;
        gcon.gridheight = 1;

        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setText("");
        chatScroll = new JScrollPane(chatArea);
        mainLayout.setConstraints(chatScroll, gcon);
        add(chatScroll);

        gcon.gridx = 1;
        gcon.gridy = 1;
        userList = new JTextArea(20, 10);
        userList.setEditable(false);
        userList.setLineWrap(true);
        userList.setWrapStyleWord(true);
        userList.setText("");
        JScrollPane userScroll = new JScrollPane(userList);

        mainLayout.setConstraints(userScroll, gcon);
        add(userScroll);

        gcon.gridx = 0;
        gcon.gridy = 2;
        chatInput = new JTextArea(5, 30);
        chatInput.setText("");
        chatInput.setEditable(true);
        chatInput.setLineWrap(true);
        chatInput.setWrapStyleWord(true);
        chatInput.setFont(new Font("Serif", Font.PLAIN, 18));
        JScrollPane inputScroll = new JScrollPane(chatInput);
        mainLayout.setConstraints(inputScroll, gcon);
        add(inputScroll);
    }

    private void setupButton() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.insets = new Insets(15, 15, 15, 15);
        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Serif", Font.PLAIN, 18));
        sendButton.addActionListener(e -> {
            inputListener.onSendButtonClicked(chatInput.getText());
            resetInput();
        });

        gcon.gridx = 1;
        gcon.gridy = 2;
        gcon.gridwidth = 1;
        gcon.gridheight = 1;
        gcon.fill = GridBagConstraints.BOTH;
        sendButton.setSize(new Dimension(50, 50));
        mainLayout.setConstraints(sendButton, gcon);
        add(sendButton);
    }
}