package org.example.gui;

import org.example.game.player.Player;

import javax.swing.*;

public class WindowToChooseNick extends JFrame {
    private JTextField textField;
    private JComboBox<String> comboBox;
    Player player;

    public WindowToChooseNick(Player player) {
        super("Chose nick");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Input your nick:");
        panel.add(label);
        textField = new JTextField(20);
        panel.add(textField);
        JButton button = new JButton("Join");
        this.player = player;

        // Define the options
        JLabel labelOptions = new JLabel("Choose control options:");
        panel.add(labelOptions);
        String[] options = {"Arrays", "W-A-S-D", "I-J-K-L","1-2-3-4"};
        comboBox = new JComboBox<>(options);
        panel.add(comboBox);

        button.addActionListener(e -> {
            String playerName = textField.getText();
            player.setName(playerName);
            player.setController((String) comboBox.getSelectedItem());
            dispose();
        });
        panel.add(button);
        panel.setSize(300, 100);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }


}
