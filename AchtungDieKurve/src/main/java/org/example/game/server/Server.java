package org.example.game.server;

import org.example.game.Game;
import org.example.game.player.PlayerHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static final int PORT = 1234;
    private ServerSocket serverSocket;

    private Game game;

    public Server(Game game) {
        try {
            serverSocket = new ServerSocket(PORT);
            this.game = game;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(new Game(new ArrayList<>()));
        System.out.println("Server started");
        while(true){
            Socket playerSocket = server.serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

            PlayerHandler clientHandler = new PlayerHandler(playerSocket, in, out);
            Thread playerHandler = new Thread(clientHandler);
            playerHandler.start();
//            game.getPlayers().s
        }
    }

    public String sendMessageAboutConnectedPlayer(){
        return game.infoAboutPlayers();
    }

}
