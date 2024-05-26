package org.example.gui;

import org.example.game.player.Direction;
import org.example.game.player.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyAdapter extends KeyAdapter {
    private Player player;

    public MyKeyAdapter(Player player) {
        this.player = player;
    }

    public void keyPressed(KeyEvent e) {
        if(player.getUp()==e.getKeyCode()) {
            player.setDirection(Direction.UP);
        }
        else if (player.getDown()==e.getKeyCode()) {
            player.setDirection(Direction.DOWN);
        }
        else if (player.getLeft()==e.getKeyCode()) {
            player.setDirection(Direction.LEFT);
        }
        else if (player.getRight()==e.getKeyCode()) {
            player.setDirection(Direction.RIGHT);
        }

    }
}
