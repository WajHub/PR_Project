package org.example.gui;

import lombok.Getter;
import org.example.game.player.Player;
import org.json.simple.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardPanel extends JPanel  {
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 600;

    public static final int PLAYER_SIZE = 100;

    public static final Color [] COLORS  = {Color.CYAN, Color.RED, Color.GREEN, Color.YELLOW};

    BoardPanel(){
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setLayout(new FlowLayout());
    }


    public void drawPlayer(Player player) {
        Graphics g = this.getGraphics();
        g.setColor(COLORS[player.getId()]);
        g.fillRect(player.getPosition().getX()*PLAYER_SIZE,
                player.getPosition().getY()*PLAYER_SIZE,
                PLAYER_SIZE, PLAYER_SIZE);
    }

    public void clearBoard() {
        Graphics g = this.getGraphics();
        g.setColor(Color.black);
        for(int i = 0; i < BOARD_HEIGHT/PLAYER_SIZE; i++){
            for(int j = 0; j < BOARD_WIDTH/PLAYER_SIZE; j++){
                g.fillRect(i*PLAYER_SIZE, j*PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE);
            }
        }
    }

    public void printBoard(JSONArray jsonArrayBoard) {
        Graphics g = this.getGraphics();
        g.setColor(Color.black);
        for(int i = 0; i < BOARD_HEIGHT/PLAYER_SIZE; i++){
            for(int j = 0; j < BOARD_WIDTH/PLAYER_SIZE; j++){
                JSONArray jsonArray = (JSONArray) jsonArrayBoard.get(i);
                int value = ((Long) jsonArray.get(j)).intValue();
                if(value == -1) g.setColor(Color.black);
                else g.setColor(COLORS[value]);
                g.fillRect(j*PLAYER_SIZE, i*PLAYER_SIZE, PLAYER_SIZE, PLAYER_SIZE);
            }
        }
    }
}
