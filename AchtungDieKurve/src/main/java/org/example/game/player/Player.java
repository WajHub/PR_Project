package org.example.game.player;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.example.game.server.Server;
import org.example.gui.GameFrame;
import org.example.gui.WindowToChoseNick;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;


@Getter
@Setter
public class Player implements Serializable {
    private transient Socket socket;
    private transient ObjectInputStream in;
    private transient ObjectOutputStream out;
    private transient JSONParser parser = new JSONParser();
    private transient Gson gson = new Gson();
    private transient JSONObject messageToServerJson = new JSONObject();
    private transient Color color;

    private String name;
    private int id;
    private boolean isAlive = false;
    private boolean isReady = false;
    private boolean connected = false;

    // TODO gracz powininen przechowywac jeszcze chyba ilosc punktow

    public Player() throws IOException {
        socket = new Socket ("127.0.0.1", Server.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;
        id = -1;
    }

    public Player(String name, int id, boolean isAlive, boolean connected, boolean isReady) {
        this.name = name;
        this.id = id;
        this.isAlive = isAlive;
        this.connected = connected;
        this.isReady = isReady;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException, ClassNotFoundException {
        Player player = new Player();
        GameFrame gameFrame = new GameFrame(player);
        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closed");
                player.close();
            }
        });


        player.choseNick();

        while(player.getName()==null){
            Thread.sleep(1000);
        }

        player.sendPlayer("newPlayer");
        player.action(gameFrame);

    }

    public void action(GameFrame gameFrame) throws InterruptedException, IOException, ClassNotFoundException, ParseException {
        while(this.connected){
            // Read message
            String messageFromServer = (String) this.in.readObject();
            JSONObject jsonMessageFromServer = (JSONObject) this.parser.parse(messageFromServer);
            String messageType = (String) jsonMessageFromServer.get("type");

            switch (messageType) {
                case "newId":
                    JSONObject playerJsonFromServer = (JSONObject) this.parser.parse((String) jsonMessageFromServer.get("content"));
                    this.setId(((Long) playerJsonFromServer.get("id")).intValue());
                    gameFrame.displayButtonReady();
                    System.out.println(this);
                    break;
                case "connectedPlayers":
                    // TODO: Poprawic sposob wyswietlania graczy (np. Nick: [ilosc punktow])
                    JSONArray connectedPlayers = (JSONArray) this.parser.parse((String) jsonMessageFromServer.get("content"));
                    String connectedPlayersStr = connectedPlayers.toString();
                    connectedPlayersStr = connectedPlayersStr.replace("}", "]\n");
                    gameFrame.displayConnectedPlayers(connectedPlayersStr);
                    break;
                default:
                    break;
            }

            // Write message
            Thread.sleep(2000);
        }
    }

    // Messages ------------------------------------------
    public void sendPlayer(String type) throws IOException {
        this.messageToServerJson.put("type", type);
        this.messageToServerJson.put("content", this.gson.toJson(this)); // Send player as json
        this.out.writeObject(this.messageToServerJson.toString());
        this.messageToServerJson.keySet().clear();
    }
    // ---------------------------------------------------

    public static Player getPlayerFromJSON(JSONObject json){
        return  new Player((String) json.get("name"),
                ((Long) json.get("id")).intValue(),
                (Boolean) json.get("isAlive"),
                (Boolean) json.get("connected"),
                (Boolean) json.get("isReady"));
    }

    // TODO Gracz musi wybrac niezajęty i niepusty nick
    public void choseNick() throws IOException {
        new WindowToChoseNick(this);
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }


    private void close () {
        try {
            connected = false;
            out.writeObject("exit");
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
