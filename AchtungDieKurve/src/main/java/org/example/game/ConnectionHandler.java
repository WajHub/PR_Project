package org.example.game;

import com.sun.jdi.event.ThreadDeathEvent;
import org.example.game.player.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Game game;

    public ConnectionHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Game game){
        this.socket = socket;
        this.in = objectInputStream;
        this.out = objectOutputStream;
        this.game = game;
    }

    @Override
    public void run() {
        try{
            System.out.println("Client connected: "+socket.getInetAddress());
            var helloMessage = (String) in.readObject();
            System.out.println(helloMessage);
            out.writeObject("Hello from server");
            while(true){
                var message = in.readObject();
                if(message.equals("exit")){
                    break;
                }
                else if (message instanceof Player player){
                    game.addPlayer(player);
                    player.setId(game.getPlayersCount());
                    player.choseNick();
                }
                System.out.println(game.infoAboutPlayers());
            }
        } catch (SocketException se) {
            close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        System.out.println("Client disconnected: "+socket.getInetAddress());
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
