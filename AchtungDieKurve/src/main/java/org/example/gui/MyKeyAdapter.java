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
            if(player.getPrevDirection()!=Direction.DOWN){
                player.setDirection(Direction.UP);
                player.setPrevDirection(player.getDirection());
            }
        }
        else if (player.getDown()==e.getKeyCode()) {
            if(player.getPrevDirection()!=Direction.UP){
                player.setDirection(Direction.DOWN);
                player.setPrevDirection(player.getDirection());
            }
        }
        else if (player.getLeft()==e.getKeyCode()) {
            if(player.getPrevDirection()!=Direction.RIGHT){
                player.setDirection(Direction.LEFT);
                player.setPrevDirection(player.getDirection());
            }
        }
        else if (player.getRight()==e.getKeyCode()) {
            if(player.getPrevDirection()!=Direction.LEFT){
                player.setDirection(Direction.RIGHT);
                player.setPrevDirection(player.getDirection());
            }
        }

    }
}
