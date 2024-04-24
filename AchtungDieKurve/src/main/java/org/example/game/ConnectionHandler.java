package org.example.game;

import com.google.gson.Gson;
import org.example.game.player.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
            JSONObject messageToClient = new JSONObject();
            JSONParser parser = new JSONParser();
            Gson gson = new Gson();

            while(true){
                // Read message
                String messageFromClient = (String) in.readObject();
                JSONObject jsonMessageFromClient = (JSONObject) parser.parse(messageFromClient);

                if(jsonMessageFromClient.get("type").equals("exit")){
                    break;
                }
                else if(jsonMessageFromClient.get("type").equals("newPlayer")){
                    messageToClient.put("type", "newId");
                    JSONObject playerJsonFromClient = (JSONObject) parser.parse((String) jsonMessageFromClient.get("content"));
                    Player player = new Player((String) playerJsonFromClient.get("name"),
                                                game.getPlayersCount(),
                                                (Boolean) playerJsonFromClient.get("isAlive"),
                                                (Boolean) playerJsonFromClient.get("connected"));
                    game.addPlayer(player);
                    String playerInJsonToClient = gson.toJson(player);
                    messageToClient.put("content", playerInJsonToClient);
                    out.writeObject(messageToClient.toString());
                    messageToClient.keySet().clear();
                }

                // Write message
                Thread.sleep(2000);
                System.out.println(game.infoAboutPlayers());
            }
        } catch (SocketException se) {
            close();
        } catch (Exception e){
//            close();
        } finally {
            if(socket.isConnected()) close();
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
