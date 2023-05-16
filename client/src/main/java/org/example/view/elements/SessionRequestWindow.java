package org.example.view.elements;

import org.example.view.listeners.SessionRequestListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SessionRequestWindow extends JFrame {
    private final GridBagLayout mainLayout;
    private SessionRequestListener sessionRequestListener;

    private JTextArea urlArea;
    private JTextArea portArea;

    public SessionRequestWindow() {
        super("Enter connection data");
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
        urlArea.setText("");
        portArea.setText("");
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

        urlArea = new JTextArea(2, 10);
        setUpTextArea(urlArea);
        mainLayout.setConstraints(urlArea, gcon);
        add(urlArea);

        gcon.gridx = 1;
        gcon.gridy = 1;
        portArea = new JTextArea(2, 5);
        setUpTextArea(portArea);
        portArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                char ch = ke.getKeyChar();
                int len = portArea.getText().length();

                if (ch >= '0' && ch <= '9' && len <= 5 || ch == 8) {
                    portArea.setEditable(true);
                } else {
                    portArea.setEditable(false);
                }
            }
        });

        mainLayout.setConstraints(portArea, gcon);
        add(portArea);
    }

    private void setupLabels() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.fill = GridBagConstraints.CENTER;
        gcon.anchor = GridBagConstraints.SOUTH;
        gcon.insets = new Insets(15, 15, 15, 15);

        JLabel urlLabel = new JLabel("Server URL");
        urlLabel.setForeground(Color.WHITE);
        urlLabel.setFont(new Font("Serif", Font.BOLD, 25));
        gcon.gridx = 0;
        gcon.gridy = 0;
        gcon.gridwidth = 1;
        gcon.gridheight = 1;
        mainLayout.setConstraints(urlLabel, gcon);
        add(urlLabel);

        JLabel portLabel = new JLabel("Server Port");
        portLabel.setForeground(Color.WHITE);
        portLabel.setFont(new Font("Serif", Font.BOLD, 25));
        gcon.gridx = 1;
        mainLayout.setConstraints(portLabel, gcon);
        add(portLabel);
    }

    private void setupButton() {
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.fill = GridBagConstraints.HORIZONTAL;
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.insets = new Insets(15, 15, 15, 15);
        JButton connectButton = new JButton("Request Connection");
        connectButton.setFont(new Font("Serif", Font.PLAIN, 18));
        connectButton.addActionListener(e -> {
            String url = urlArea.getText();
            String port = portArea.getText();
            if (!url.isBlank() && !port.isBlank()) {
                sessionRequestListener.onSessionStartRequested(url, Integer.parseInt(port));
            }
        });

        gcon.gridx = 0;
        gcon.gridy = 2;
        gcon.gridwidth = 2;
        gcon.gridheight = 1;

        connectButton.setSize(new Dimension(50, 50));
        mainLayout.setConstraints(connectButton, gcon);
        add(connectButton);
    }

    private void setUpTextArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("");
    }

    public void setSessionRequestListener(SessionRequestListener sessionRequestListener) {
        this.sessionRequestListener = sessionRequestListener;
    }
}
