package org.example.game;

import lombok.Getter;
import org.example.game.player.Player;
import org.example.gui.BoardPanel;

import org.example.game.player.Direction;
import java.util.List;

@Getter
public class Game {

    List<Player> players;
    int[][] board = new int [BoardPanel.BOARD_HEIGHT/BoardPanel.PLAYER_SIZE][BoardPanel.BOARD_WIDTH/BoardPanel.PLAYER_SIZE];
    boolean isStared = false;

    public Game(List<Player> players) {
        this.players = players;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                board[i][j] = -1;
            }
        }
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

    public void newDirectionPlayer(int id, Direction direction){
        players.get(id).setDirection(direction);
    }

    public void setDirection(int id, Direction direction) {
        players.get(id).setDirection(direction);
    }

    public synchronized void savePositions() {
        players.stream().forEach(player -> {
            int x = player.getPosition().getX();
            int y = player.getPosition().getY();
            if(validatePosition(x, y)==0) board[x][y] = player.getId();
            else player.setAlive(false);
        });
    }

    private int validatePosition(int x, int y) {
        if(x < 0 || x >= board.length || y < 0 || y >= board[0].length){
            return -1;
        }
        if(board[x][y] >= 0){
            return -1;
        }
        return 0;
    }

    public void clearBoard() {
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                board[i][j] = -1;
            }
        }
    }
}
