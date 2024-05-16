package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {
    static final int MENU_HEIGHT = 75;

    static final int MENU_WIDTH = BoardPanel.BOARD_WIDTH;

    static JLabel playersConnected;

    MenuPanel(){
        this.setFocusable(true);
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        this.addKeyListener(new MyKeyAdapter());
        playersConnected = new JLabel("none");
        this.add(playersConnected);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void displayConnectedPlayers(String players) {
        playersConnected.setText(players);
    }
}
