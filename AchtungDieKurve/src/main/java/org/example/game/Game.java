package org.example.game;

import org.example.game.player.Player;

import java.util.List;

public class Game {

    List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public String infoAboutPlayers(){
        StringBuilder result  = new StringBuilder("Connected player: \n");
        players.stream().forEach(player -> result.append(player + "\n"));
        return result.toString();
    }

    public List<Player> getPlayers() {
        return players;
    }
}
