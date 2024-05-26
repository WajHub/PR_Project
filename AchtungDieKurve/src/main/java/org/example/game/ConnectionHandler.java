package org.example.game;

import com.google.gson.Gson;
import org.example.game.player.Direction;
import org.example.game.player.Player;
import org.example.game.player.Position;
import org.example.game.server.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private transient JSONParser parser = new JSONParser();
    private transient Gson gson = new Gson();
    private transient JSONObject messageToPlayersJson = new JSONObject();
    private boolean isRunning = true;

    public ConnectionHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        this.socket = socket;
        this.in = objectInputStream;
        this.out = objectOutputStream;
    }

    @Override
    public void run() {
        try{

            while(isRunning){
                // Read message
                String messageFromClient = (String) in.readObject();
                JSONObject jsonMessageFromClient = (JSONObject) parser.parse(messageFromClient);
                String messageType = (String) jsonMessageFromClient.get("type");

                switch (messageType) {
                    case "exit":
                        isRunning = false;
                        break;
                    case "newPlayer":
                        JSONObject playerJsonFromClient = (JSONObject) parser.parse((String) jsonMessageFromClient.get("content"));
                        Player player = Player.getPlayerFromJSON(playerJsonFromClient);
                        player.setId(Server.game.getPlayers().size());
                        messageToPlayersJson.put("type", "newId");
                        addNewPlayer(player);
                        sendPlayers("connectedPlayers");
                        break;
                    case "ready":
                        Player player2 = Player.getPlayerFromJSON((JSONObject) parser.parse((String) jsonMessageFromClient.get("content")));
                        Server.game.getPlayers().forEach(p ->{
                            if (p.getId()==player2.getId()) p.setReady(true);
                        });
                        sendConnectedPlayers();
                        startRound();
                        break;
                    case "getDirection":
                        Player player3 = Player.getPlayerFromJSON((JSONObject) parser.parse((String) jsonMessageFromClient.get("content")));
                        Server.game.getPlayers().forEach(p ->{
                            if (p.getId()==player3.getId()) p.setDirection(player3.getDirection());
                        });
                        break;
                    case "pong":
                        System.out.println("Pong from: "+socket.getInetAddress() + messageFromClient);
                        break;
                    default:
                        // Handle unexpected messageType
                        break;
                }
                if(Server.game.isStared()){
                    Thread.sleep(1000);
                    getDirectionsPlayers();
                    movePlayers();
                    sendPlayers("newPositions");
                }
            }
        } catch (SocketException se) {
            close();
        } catch (Exception ignored){
        } finally {
            if(socket.isConnected()) close();
        }
    }



    private void startRound() throws IOException, InterruptedException {
        if(everyPlayerIsReady() && Server.game.getPlayersCount() > 1){
            Server.game.setStared(true);
            Server.game.getPlayers().forEach(p -> p.setPosition(Position.randomPosition()));
            sendPlayers("newPositions");
            Thread.sleep(3000);
        }
    }


    private void movePlayers() {
        Server.game.getPlayers().forEach(
                player -> {
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
        );
    }

    private boolean everyPlayerIsReady() {
        return Server.game.getPlayers().stream().allMatch(Player::isReady);
    }

    private void addNewPlayer(Player player ) throws IOException {
        Server.game.addPlayer(player);
        String playerInJsonToClient = gson.toJson(player);
        messageToPlayersJson.put("content", playerInJsonToClient);
        out.writeObject(messageToPlayersJson.toString());
        messageToPlayersJson.keySet().clear();
    }

    private void sendPlayers(String type) throws IOException {
        for(ConnectionHandler  client: Server.clients){
            messageToPlayersJson.put("type", type);
            messageToPlayersJson.put("content", gson.toJson(Server.game.getPlayers()));
            client.out.writeObject(messageToPlayersJson.toString());
            messageToPlayersJson.keySet().clear();
        }
    }

    private void getDirectionsPlayers() throws IOException, InterruptedException {
        for(ConnectionHandler  client: Server.clients){
            messageToPlayersJson.put("type", "getDirection");
            client.out.writeObject(messageToPlayersJson.toString());
            Thread.sleep(10);
            messageToPlayersJson.keySet().clear();
        }
    }

    private void sendPing() throws IOException, InterruptedException {
        for(ConnectionHandler  client: Server.clients){
            messageToPlayersJson.put("type", "ping");
            messageToPlayersJson.put("content", "ping");
            client.out.writeObject(messageToPlayersJson.toString());
            Thread.sleep(10);
            messageToPlayersJson.keySet().clear();
        }
    }

    private void sendConnectedPlayers() throws IOException {
        for(ConnectionHandler  client: Server.clients){
            messageToPlayersJson.put("type", "connectedPlayers");
            messageToPlayersJson.put("content", gson.toJson(Server.game.getPlayers()));
            client.out.writeObject(messageToPlayersJson.toString());
            messageToPlayersJson.keySet().clear();
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
