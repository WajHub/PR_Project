package org.example.game.player;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.example.game.server.Server;
import org.example.gui.GameFrame;
import org.example.gui.WindowToChooseNick;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.awt.*;
import java.awt.event.KeyEvent;
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
    private transient int up;
    private transient int down;
    private transient int left;
    private transient int right;


    private String name;
    private int id;
    private boolean isAlive = false;
    private boolean isReady = false;
    private boolean connected = false;
    private Position position = new Position();
    private Direction direction = Direction.DOWN;

    // TODO gracz powininen przechowywac jeszcze chyba ilosc punktow

    public Player() throws IOException {
        socket = new Socket ("127.0.0.1", Server.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;
        id = -1;
    }

    public Player(String name, int id, boolean isAlive, boolean connected, boolean isReady, Position position) {
        this.name = name;
        this.id = id;
        this.isAlive = isAlive;
        this.connected = connected;
        this.isReady = isReady;
        this.position = position;
    }

    public Player(String name, int id, boolean isAlive, boolean connected, boolean isReady, Position position, Direction direction) {
        this.name = name;
        this.id = id;
        this.isAlive = isAlive;
        this.connected = connected;
        this.isReady = isReady;
        this.position = position;
        this.direction = direction;
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
            Thread.sleep(10);
        }

        player.sendPlayer("newPlayer");

        player.action(gameFrame);


    }

    public void action(GameFrame gameFrame) throws IOException, ClassNotFoundException, ParseException {
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
                    break;
                case "connectedPlayers":
                    // TODO: Poprawic sposob wyswietlania graczy (np. Nick: [ilosc punktow])
                    JSONArray connectedPlayers = (JSONArray) this.parser.parse((String) jsonMessageFromServer.get("content"));
                    String connectedPlayersStr = connectedPlayers.toString();
                    connectedPlayersStr = connectedPlayersStr.replace("}", "]\n");
                    gameFrame.displayConnectedPlayers(connectedPlayersStr);
                    break;
                case "newPositions":
                    JSONArray newPositions = (JSONArray) this.parser.parse((String) jsonMessageFromServer.get("content"));
                    drawPlayers(newPositions, gameFrame);
                    break;
                case "getDirection":
                    this.messageToServerJson.put("type", "getDirection");
                    this.messageToServerJson.put("content", this.gson.toJson(this)); // Send player as json
                    this.out.writeObject(this.messageToServerJson.toString());
                    this.messageToServerJson.keySet().clear();
                    break;
                case "ping":
                    this.messageToServerJson.put("type", "pong");
                    this.messageToServerJson.put("content", this.gson.toJson(this));
                    this.out.writeObject(this.messageToServerJson.toString());
                    this.messageToServerJson.keySet().clear();
                    System.out.println("Ping" + messageFromServer + this);
                    break;
                default:
                    break;
            }
        }
    }

    private void drawPlayers(JSONArray newPositions, GameFrame gameFrame) {
        newPositions.forEach(player -> {
            JSONObject playerJson = (JSONObject) player;
            Player playerFromJson = Player.getPlayerFromJSON(playerJson);
            gameFrame.drawPlayer(playerFromJson);
        });
    }

    public void sendPlayer(String type) throws IOException {
        this.messageToServerJson.put("type", type);
        this.messageToServerJson.put("content", this.gson.toJson(this)); // Send player as json
        this.out.writeObject(this.messageToServerJson.toString());
        this.messageToServerJson.keySet().clear();
    }

    public static Player getPlayerFromJSON(JSONObject json){
        return  new Player((String) json.get("name"),
                ((Long) json.get("id")).intValue(),
                (Boolean) json.get("isAlive"),
                (Boolean) json.get("connected"),
                (Boolean) json.get("isReady"),
                Position.getPositionFromJSON((JSONObject) json.get("position")),
                Direction.valueOf((String) json.get("direction")));
    }

    // TODO Gracz musi wybrac niezajęty i niepusty nick
    public void choseNick() throws IOException {
        new WindowToChooseNick(this);
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


    public void setController(String selectedItem) {
        switch (selectedItem){
            case "W-A-S-D":
                up = KeyEvent.VK_W;
                down = KeyEvent.VK_S;
                left = KeyEvent.VK_A;
                right = KeyEvent.VK_D;
                break;
            case "I-J-K-L":
                up = KeyEvent.VK_I;
                down = KeyEvent.VK_K;
                left = KeyEvent.VK_J;
                right = KeyEvent.VK_L;
                break;
            case "1-2-3-4":
                up = KeyEvent.VK_1;
                down = KeyEvent.VK_2;
                left = KeyEvent.VK_3;
                right = KeyEvent.VK_4;
                break;
            default:
                // Default control -> arrays
                up = KeyEvent.VK_UP;
                down = KeyEvent.VK_DOWN;
                left = KeyEvent.VK_LEFT;
                right = KeyEvent.VK_RIGHT;
                break;
        }
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
