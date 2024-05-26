package org.example.gui;
import org.example.game.player.Player;

import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private Player player;
    public GameFrame(Player player){
        gamePanel = new GamePanel(player);
        this.player = player;
        this.add(gamePanel);
        this.setTitle("Achtung Die Kurve");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLocation(300,50);
        this.setResizable(false);
        this.pack();
    }


    public void displayConnectedPlayers(String players) {
        this.gamePanel.displayConnectedPlayers(players);
    }

    public void displayButtonReady() {
        gamePanel.displayButtonReady();
    }

    public void drawPlayer(Player player) {
        gamePanel.drawPlayer(player);
    }
}