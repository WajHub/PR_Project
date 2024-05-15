package org.example.game;

import com.google.gson.Gson;
import org.example.game.player.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

// Game trzeba stad usunac. Zarzadzanie Gra powinno byc w klasie Server (ale nie wiem jak to zrobic, bo server jest blokowany
// przez metode accept
public class ConnectionHandler implements Runnable{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Game game;

    private transient JSONParser parser = new JSONParser();
    private transient Gson gson = new Gson();
    private transient JSONObject messageToPlayersJson = new JSONObject();

    public ConnectionHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Game game){
        this.socket = socket;
        this.in = objectInputStream;
        this.out = objectOutputStream;
        this.game = game;
    }

    @Override
    public void run() {
        try{

            while(true){
                // Read message
                String messageFromClient = (String) in.readObject();
                JSONObject jsonMessageFromClient = (JSONObject) parser.parse(messageFromClient);

                if(jsonMessageFromClient.get("type").equals("exit")){
                    break;
                }
                else if(jsonMessageFromClient.get("type").equals("newPlayer")){
                    messageToPlayersJson.put("type", "newId");
                    JSONObject playerJsonFromClient = (JSONObject) parser.parse((String) jsonMessageFromClient.get("content"));
                    Player player = new Player((String) playerJsonFromClient.get("name"),
                                                game.getPlayersCount(),
                                                (Boolean) playerJsonFromClient.get("isAlive"),
                                                (Boolean) playerJsonFromClient.get("connected"));
                    game.addPlayer(player);
                    String playerInJsonToClient = gson.toJson(player);
                    messageToPlayersJson.put("content", playerInJsonToClient);
                    out.writeObject(messageToPlayersJson.toString());
                    messageToPlayersJson.keySet().clear();
                }

                // Write message
                Thread.sleep(2000);

                System.out.println(game.infoAboutPlayers());
            }
        } catch (SocketException se) {
            close();
        } catch (Exception e){
        } finally {
            if(socket.isConnected()) close();
        }
    }

    public void sendConnectedPlayers(List<Player> players) throws IOException {
        this.messageToPlayersJson.put("type", "connectedPlayers");
        this.messageToPlayersJson.put("content", this.gson.toJson(players)); // Send player as json
        this.out.writeObject(this.messageToPlayersJson.toString());
        this.messageToPlayersJson.keySet().clear();
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
