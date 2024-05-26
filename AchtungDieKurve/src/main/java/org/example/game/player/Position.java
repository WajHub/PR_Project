package org.example.game.player;

import lombok.Getter;
import lombok.Setter;
import org.example.gui.BoardPanel;
import org.json.simple.JSONObject;

@Getter
@Setter
public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(){
        this.x = 0;
        this.y = 0;
    }

    public static Position randomPosition(){
        int x = (int) (Math.random() *(BoardPanel.BOARD_WIDTH/BoardPanel.PLAYER_SIZE));
        int y = (int) (Math.random() * (BoardPanel.BOARD_HEIGHT/BoardPanel.PLAYER_SIZE));
        return new Position(x, y);
    }

    public static Position getPositionFromJSON(JSONObject position) {
        int x = ((Long) position.get("x")).intValue();
        int y = ((Long) position.get("y")).intValue();
        return new Position(x, y);
    }
}
