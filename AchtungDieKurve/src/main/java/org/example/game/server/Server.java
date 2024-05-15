package org.example.game.server;

import org.example.game.Game;
import org.example.game.ConnectionHandler;
import org.example.game.player.Player;

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

    private ArrayList<ConnectionHandler> clients;

    public Server() {
        try {
            game = new Game(new ArrayList<>());
            serverSocket = new ServerSocket(PORT);
            clients = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Server server = new Server();
        while(true){
            Socket playerSocket = server.serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

            ConnectionHandler connectionHandler = new ConnectionHandler(playerSocket, in, out, server.game);
            Thread playerThread = new Thread(connectionHandler);
            server.clients.add(connectionHandler);
            playerThread.start();

            // Send Message
            server.sendConnectedPlayers(server.game);

        }
    }

    private void sendConnectedPlayers(Game game) throws IOException {
        for (ConnectionHandler client : clients) {
            client.sendConnectedPlayers(game.getPlayers());
        }
    }

}
