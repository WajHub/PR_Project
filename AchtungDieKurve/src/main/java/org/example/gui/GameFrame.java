package org.example.gui;
import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    public GameFrame(){
        gamePanel = new GamePanel();
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
}