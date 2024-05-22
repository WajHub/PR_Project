package org.example.game;

import com.google.gson.Gson;
import org.example.game.player.Player;
import org.example.game.server.Server;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

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
                        sendConnectedPlayers();
                        break;
                    case "ready":
                        Player player2 = Player.getPlayerFromJSON((JSONObject) parser.parse((String) jsonMessageFromClient.get("content")));
                        System.out.println("Playerid: "+player2.getId()+ " is ready");
                        for (var pl: Server.game.getPlayers()){
                            if (player2.getId()==pl.getId()){
                                pl.setReady(true);
                            }
                        }
                        Server.game.getPlayers().forEach(p ->{
                            if (p.getId()==player2.getId()) p.setReady(true);
                        });
                        sendConnectedPlayers();
                        break;
                    default:
                        // Handle unexpected messageType
                        break;
                }
                Thread.sleep(2000);
                System.out.println(Server.game.infoAboutPlayers());
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
