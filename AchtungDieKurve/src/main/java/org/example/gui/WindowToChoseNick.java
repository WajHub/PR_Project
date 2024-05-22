package org.example.gui;

import org.example.game.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WindowToChoseNick extends JFrame {
    private JTextField textField;
    Player player;

    public WindowToChoseNick(Player player) {

        super("Chose nick");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Input your nick:");
        panel.add(label);
        textField = new JTextField(20);
        panel.add(textField);
        JButton button = new JButton("Join");
        this.player = player;

        button.addActionListener(e -> {
            String playerName = textField.getText();
            player.setName(playerName);
            dispose();
        });
        panel.add(button);
        panel.setSize(300, 100);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }


}
