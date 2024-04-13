package org.example.game;

import lombok.Getter;
import org.example.game.player.Player;

import java.util.List;

@Getter
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


    public int getPlayersCount() {
        return players.size();
    }

}
