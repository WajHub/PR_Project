package org.example.gui;

import org.example.game.player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private MenuPanel menuPanel;
    private BoardPanel boardPanel;
    private Player player;
    GamePanel(Player player){
        this.player = player;
        this.setFocusable(true);
        this.setBackground(Color.white);
        int screen_width = BoardPanel.BOARD_WIDTH;
        int screen_height = BoardPanel.BOARD_HEIGHT+MenuPanel.MENU_HEIGHT;
        this.setPreferredSize(new Dimension(screen_width, screen_height));
        this.addKeyListener(new MyKeyAdapter(player));
        this.setLayout(new FlowLayout());
        menuPanel = new MenuPanel(player);
        this.add(menuPanel);
        boardPanel = new BoardPanel();
        this.add(boardPanel);
        this.setBorder(new EmptyBorder(0, 0,0,0));
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void displayConnectedPlayers(String players) {
        menuPanel.displayConnectedPlayers(players);
    }

    public void displayButtonReady() {
        menuPanel.displayButtonReady();
    }

    public void drawPlayer(Player player) {
        boardPanel.drawPlayer(player);
    }

    public void clearBoard() {
        boardPanel.clearBoard();
    }
}
