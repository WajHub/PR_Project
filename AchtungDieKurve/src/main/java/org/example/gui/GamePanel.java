package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {
    private final int SCREEN_WIDTH = 600;
    private final int SCREEN_HEIGHT = 600;

    GamePanel(){
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.addKeyListener(new MyKeyAdapter());
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
