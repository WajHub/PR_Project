package org.example.game.player;

import lombok.Getter;
import lombok.Setter;
import org.example.game.Game;
import org.example.game.server.Server;
import org.example.gui.GameFrame;
import org.example.gui.WindowToChoseNick;



import javax.swing.*;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

@Getter
@Setter
public class Player implements Serializable {
    private transient Socket socket;
    private transient ObjectInputStream in;
    private transient ObjectOutputStream out;
    private GameFrame gameFrame;
    String name;
    int id;
    private boolean isAlive = false;
    private boolean connected = false;

    public Player(GameFrame gameFrame) throws IOException {
        socket = new Socket ("127.0.0.1", Server.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;
        this.gameFrame = gameFrame;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Player player = new Player(new GameFrame());
        player.out.writeObject("Hello from player (" + player + ")");
        player.gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closed");
                player.close();
            }
        });

        var helloMess = player.in.readObject();
        System.out.println(helloMess);

        player.choseNick();

        String typMess = "newPlayer";
        player.out.writeObject(typMess);
        player.out.writeObject(player);

        while(player.connected){
            // Read message
            var message = player.in.readObject();
            if(message.equals("newId")){
                var newPlayer = player.in.readObject();
                if (newPlayer instanceof Player playerChanged){
                    player.setName(playerChanged.getName());
                    player.setId(playerChanged.getId());
                }
            }

            // Write message
            Thread.sleep(2000);
            player.out.writeObject("test wyslania");
            System.out.println(player);
        }

    }

    public void choseNick() throws IOException {
        new WindowToChoseNick(this);
    }

    private void close () {
        try {
            connected = false;
            out.writeObject("exit");
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
