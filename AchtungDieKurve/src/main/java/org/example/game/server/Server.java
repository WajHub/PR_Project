package org.example.game.server;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.example.game.Game;
import org.example.game.ConnectionHandler;
import org.example.game.player.Direction;
import org.example.game.player.Player;
import org.example.game.player.Position;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public class Server {
    public static final int PORT = 1234;
    public static int TIME_FOR_MOVE = 1000;
    public final int ATTEMPT_TO_RECONNECT = 10; // wait 10 seconds for reconnect
    private ServerSocket serverSocket;
    public static Game game;
    public static ArrayList<ConnectionHandler> clients;  // TODO: zamienic na threadpool
    private static JSONObject messageToPlayersJson = new JSONObject();
    private static final Gson gson = new Gson();

    public Server() {
        try {
            game = new Game(new ArrayList<>());
            serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(1000); // Set timeout to 1 seconds
            clients = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        while (true) {
            // Wait for connect players
            while (!game.isStared()) {
                try {
                    Socket playerSocket = server.serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

                    ConnectionHandler connectionHandler = new ConnectionHandler(playerSocket, in, out);
                    Thread playerThread = new Thread(connectionHandler);
                    server.clients.add(connectionHandler);
                    playerThread.start();
                } catch (SocketTimeoutException e) {
                    // Check disconnected players
                    while(!everyPlayersConnected()) server.waitForReconnect();
                    startRound();
                }
            }
            while (game.isStared()) {
                // Check disconnected players
                while(!everyPlayersConnected()) server.waitForReconnect();

                sendPlayers("newPositions");
                sendPlayers("getDirectionFromPlayer");
                movePlayers();
                game.savePositions();
                sendDeadPlayers();
                checkEndOfRound();
                sendPlayers("connectedPlayers");
                Thread.sleep(TIME_FOR_MOVE);
            }
        }
    }

    public static void startRound() throws IOException, InterruptedException {
        if(everyPlayerIsReady() && Server.game.getPlayersCount() > 1){
            Server.game.setStared(true);
            sendPlayers("startRound");
            Server.game.getPlayers().forEach(p -> p.setPosition(Position.randomPosition()));
            Server.game.savePositions();
            sendPlayers("newPositions");
            Thread.sleep(2000);
        }
    }

    private static void checkEndOfRound() throws IOException {
        if(game.getPlayers().stream().filter(Player::isAlive).count() <= 1){
            game.getPlayers().stream().filter(Player::isAlive).forEach(player -> player.setPoints(player.getPoints()+1));
            game.setStared(false);
            game.getPlayers().forEach(player -> {
                player.setAlive(true);
                player.setReady(false);
            });
            sendPlayers("endOfRound");
            game.clearBoard();
        }
    }

    private static void sendDeadPlayers() throws IOException {
        for(Player player: Server.game.getPlayers()){
            if(!player.isAlive()){
                for(ConnectionHandler  client: clients){
                    messageToPlayersJson.put("type", "deadPlayer");
                    messageToPlayersJson.put("content", gson.toJson(player));
                    client.out.writeObject(messageToPlayersJson.toString());
                    messageToPlayersJson.keySet().clear();
                }
            }
        }
    }

    private static void sendPing() throws IOException, InterruptedException {
        for(ConnectionHandler  client: Server.clients) {
            messageToPlayersJson.put("type", "ping");
            messageToPlayersJson.put("content", "ping");
            client.out.writeObject(messageToPlayersJson.toString());
            Thread.sleep(10);
            messageToPlayersJson.keySet().clear();
        }
    }

    public static void sendPlayers(String type) throws IOException {
        for(ConnectionHandler  client: clients){
            messageToPlayersJson.put("type", type);
            messageToPlayersJson.put("content", gson.toJson(Server.game.getPlayers()));
            client.out.writeObject(messageToPlayersJson.toString());
            messageToPlayersJson.keySet().clear();
        }
    }

    private static void movePlayers() {
        Server.game.getPlayers().forEach(
                player -> {
                    if(player.isAlive()){
                        Position position = player.getPosition();
                        switch (player.getDirection()){
                            case Direction.UP:
                                position.setY(position.getY()-1);
                                break;
                            case Direction.DOWN:
                                position.setY(position.getY()+1);
                                break;
                            case Direction.LEFT:
                                position.setX(position.getX()-1);
                                break;
                            case Direction.RIGHT:
                                position.setX(position.getX()+1);
                                break;
                        }
                    }
                }
        );
    }

    public static void disconnectPlayer(Player player) {
        game.getPlayers().forEach(p -> {
            if(p.getId()== player.getId()){
                p.setConnected(false);
            }
        });
    }

    private void waitForReconnect() throws IOException {
        int attempt = 0;
        boolean connected = false;
        ConnectionHandler connectionHandler;
        while(attempt < this.ATTEMPT_TO_RECONNECT){
            try {
                Socket playerSocket = this.serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

                connectionHandler = new ConnectionHandler(playerSocket, in, out);
                Thread playerThread = new Thread(connectionHandler);
                this.clients.add(connectionHandler);
                playerThread.start();
                attempt = this.ATTEMPT_TO_RECONNECT;
                connected = true;

                messageToPlayersJson.put("type", "reconnectionPlayer");
                Player disconnectedPlayer = Server.game.getDisconnectedPlayer();
                game.setReconnection();
                messageToPlayersJson.put("content", gson.toJson(disconnectedPlayer));
                messageToPlayersJson.put("board", gson.toJson(game.getBoard()));
                connectionHandler.out.writeObject(messageToPlayersJson.toString());
                messageToPlayersJson.keySet().clear();
                sendPlayers("connectedPlayers");
            } catch (SocketTimeoutException e) {
                System.out.println("Waiting for reconnect ("+(this.ATTEMPT_TO_RECONNECT-attempt)+" Seconds)");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            attempt++;
        }
        if(!connected){
            // Remove player which is not connected
            game.getPlayers().removeIf(player -> !player.isConnected());
            sendPlayers("connectedPlayers");
            System.out.println("Disconnected player removed");
        }
    }

    public static boolean everyPlayersConnected(){
        return game.getPlayers().stream().allMatch(Player::isConnected);
    }

    private static boolean everyPlayerIsReady() {
        return Server.game.getPlayers().stream().allMatch(Player::isReady);
    }

}
