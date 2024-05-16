package org.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ActionListener {
    static final int MENU_HEIGHT = 125;

    static final int MENU_WIDTH = BoardPanel.BOARD_WIDTH;

    private JLabel playersConnected;
    private JTextArea textArea;

    MenuPanel(){
        this.setFocusable(true);
        this.setBackground(Color.white);
        this.setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        this.addKeyListener(new MyKeyAdapter());
        playersConnected = new JLabel("Players Connected:");
        textArea = new JTextArea();
        textArea.setRows(5);  // Set the number of rows to 5
        textArea.setColumns(40);  // Set the maximum number of characters per line to 20
        textArea.setEditable(false);  // Make the text area unmodifiable
        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(playersConnected);
        this.add(scrollPane);

    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void displayConnectedPlayers(String players) {
        textArea.setText(players);
    }
}
