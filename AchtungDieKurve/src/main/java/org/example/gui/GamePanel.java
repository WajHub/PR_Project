package org.example.gui;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private MenuPanel menuPanel;
    GamePanel(){
        this.setFocusable(true);
        this.setBackground(Color.white);
        int screen_width = BoardPanel.BOARD_WIDTH;
        int screen_height = BoardPanel.BOARD_HEIGHT+MenuPanel.MENU_HEIGHT;
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.addKeyListener(new MyKeyAdapter());
        this.setLayout(new FlowLayout());
        menuPanel = new MenuPanel();
        this.add(menuPanel);
        this.add(new BoardPanel());
        this.setBorder(new EmptyBorder(0, 0,0,0));
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void displayConnectedPlayers(String players) {
        menuPanel.displayConnectedPlayers(players);
    }

    public void displayButtonToStartGame() {
        menuPanel.displayButtonToStartGame();
    }
}
