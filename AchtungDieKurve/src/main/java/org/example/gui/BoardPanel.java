package org.example.gui;

import lombok.Getter;
import org.example.game.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardPanel extends JPanel implements ActionListener {
    public static final int BOARD_WIDTH = 600;
    public static final int BOARD_HEIGHT = 600;

    public static final int PLAYER_SIZE = 10;

    public static final Color [] COLORS  = {Color.CYAN, Color.RED, Color.GREEN, Color.YELLOW};

    BoardPanel(){
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.addKeyListener(new MyKeyAdapter());
        this.setLayout(new FlowLayout());
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void drawPlayer(Player player) {
        Graphics g = this.getGraphics();
        g.setColor(COLORS[player.getId()]);
        g.fillRect(player.getPosition().getX()*PLAYER_SIZE,
                player.getPosition().getY()*PLAYER_SIZE,
                PLAYER_SIZE, PLAYER_SIZE);
    }
}
