package org.example.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoardPanel extends JPanel implements ActionListener {
    static final int BOARD_WIDTH = 600;
    static final int BOARD_HEIGHT = 600;

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
}
