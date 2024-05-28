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
    public ObjectOutputStream out;
    private final transient JSONParser parser = new JSONParser();
    private final transient Gson gson = new Gson();
    private final transient JSONObject messageToPlayersJson = new JSONObject();
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
                        Server.sendPlayers("connectedPlayers");
                        break;
                    case "ready":
                        Player player2 = Player.getPlayerFromJSON((JSONObject) parser.parse((String) jsonMessageFromClient.get("content")));
                        Server.game.getPlayers().forEach(p ->{
                            if (p.getId()==player2.getId()) p.setReady(true);
                        });
                        Server.sendPlayers("connectedPlayers");
                        Server.startRound();
                        break;
                    case "getDirection":
                        Player player3 = Player.getPlayerFromJSON((JSONObject) parser.parse((String) jsonMessageFromClient.get("content")));
                        Server.game.getPlayers().forEach(p ->{
                            if (p.getId()==player3.getId()) p.setDirection(player3.getDirection());
                        });
                        break;
                    case "pong":
                        System.out.println("Pong from: " + messageFromClient);
                        break;
                    default:
                        // Handle unexpected messageType
                        break;
                }
            }
        } catch (SocketException se) {
            close();
        } catch (Exception ignored){
        } finally {
            if(socket.isConnected()) close();
        }
    }


    private void addNewPlayer(Player player ) throws IOException {
        Server.game.addPlayer(player);
        String playerInJsonToClient = gson.toJson(player);
        messageToPlayersJson.put("content", playerInJsonToClient);
        out.writeObject(messageToPlayersJson.toString());
        messageToPlayersJson.keySet().clear();
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
