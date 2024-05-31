package org.example.gui;

import org.example.game.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuPanel extends JPanel implements ActionListener {
    static final int MENU_HEIGHT = 140;

    static final int MENU_WIDTH = BoardPanel.BOARD_WIDTH;

    private JLabel playersConnected;
    private JLabel playerColor = new JLabel("Player color");
    private JTextArea textArea;
    private Player player;
    JButton buttonReady = new JButton("Ready");
    MenuPanel(Player player){
        this.player = player;
        this.setFocusable(true);
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        playersConnected = new JLabel("Players Connected:");
        textArea = new JTextArea();
        textArea.setRows(4);  // Set the number of rows to 5
        textArea.setColumns(40);  // Set the maximum number of characters per line to 20
        textArea.setEditable(false);  // Make the text area unmodifiable
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(playersConnected);
        this.add(scrollPane);
        this.add(playerColor);

        playerColor.setVisible(true);

        buttonReady.setVisible(false);
        buttonReady.addActionListener(e -> {
            player.setReady(true);
            try {
                player.sendPlayer("ready");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            buttonReady.setVisible(false);
            playerColor.setForeground(BoardPanel.COLORS[player.getId()]);
            playerColor.setVisible(true);
        });
        this.add(buttonReady);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void displayConnectedPlayers(String players) {
        textArea.setText(players);
    }

    public void displayButtonReady() {
        buttonReady.setVisible(true);
    }
}
