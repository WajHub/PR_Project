package org.example.game.server;

import lombok.Getter;
import lombok.Setter;
import org.example.game.Game;
import org.example.game.ConnectionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Getter
@Setter
public class Server {
    public static final int PORT = 1234;
    private ServerSocket serverSocket;
    public static Game game;
    public static ArrayList<ConnectionHandler> clients;  // TODO: zamienic na threadpool

    public Server() {
        try {
            game = new Game(new ArrayList<>());
            serverSocket = new ServerSocket(PORT);
            clients = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        while(true){
            // Wait for connect players
            while(!game.isStared()){
                Socket playerSocket = server.serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

                ConnectionHandler connectionHandler = new ConnectionHandler(playerSocket, in, out);
                Thread playerThread = new Thread(connectionHandler);
                server.clients.add(connectionHandler);
                playerThread.start();
            }
        }
    }





}
