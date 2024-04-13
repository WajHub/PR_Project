package org.example.game.player;

import org.example.game.server.Server;
import org.example.gui.GameFrame;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private GameFrame gameFrame;
    String name;
    int id;

    public Player(GameFrame gameFrame, int id, String name) throws IOException {
        socket = new Socket ("127.0.0.1", Server.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        this.gameFrame = gameFrame;
        // id i name bedzie ustawiane po polaczeniu (id udzieli serwer, a name wybierze gracz)
        this.id = id;
        this.name = name;
    }

    public static void main(String[] args) throws IOException {
        Player player = new Player(new GameFrame(), 1, "test");

        // Player close window
        player.gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.out.println("Window closed");
                player.close();
            }
        });



    }

    public void showMessage(String content){
        showMessageDialog(gameFrame, content);
    }

    private void close () {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
