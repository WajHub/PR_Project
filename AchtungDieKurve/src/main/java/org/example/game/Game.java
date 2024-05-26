package org.example.game;

import lombok.Getter;
import org.example.game.player.Player;
import org.example.gui.BoardPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    List<Player> players;
    int[][] board = new int [BoardPanel.HEIGHT/BoardPanel.PLAYER_SIZE][BoardPanel.WIDTH/BoardPanel.PLAYER_SIZE];
    boolean isStared = false;

    public Game(List<Player> players) {
        this.players = players;
    }

    public String infoAboutPlayers(){
        StringBuilder result  = new StringBuilder("Connected player: \n");
        players.stream().forEach(player -> result.append(player + "\n"));
        return result.toString();
    }

    public int getPlayersCount() {
        return players.size();
    }

    public void removePlayer(int id) {
        players.remove(id);
    }

    // Synchronized
    public synchronized Player getPlayer(int id){
        return players.get(id-1);
    }

    public synchronized void addPlayer(Player player) {
        players.add(player);
    }

    public void setStared(boolean b) {
        isStared = b;
    }
}
