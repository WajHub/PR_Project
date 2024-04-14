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
        JButton button = new JButton("OK");
        this.player = player;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = textField.getText();
                player.setName(playerName);
                String typMess = "newNick";
                try {
                    player.getOut().writeObject(typMess);
                    player.getOut().writeObject(playerName);
                    player.getOut().writeObject(player);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                dispose();
            }
        });
        panel.add(button);
        panel.setSize(300, 100);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }


}
