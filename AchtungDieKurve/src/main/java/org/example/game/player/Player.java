package org.example.game.player;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import netscape.javascript.JSObject;
import org.example.game.Game;
import org.example.game.server.Server;
import org.example.gui.GameFrame;
import org.example.gui.WindowToChoseNick;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


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
    private transient GameFrame gameFrame;
    private transient JSONParser parser = new JSONParser();
    private transient Gson gson = new Gson();
    private transient JSONObject messageToServerJson = new JSONObject();

    private String name;
    private int id;
    private boolean isAlive = false;
    private boolean connected = false;

    public Player(GameFrame gameFrame) throws IOException {
        socket = new Socket ("127.0.0.1", Server.PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;
        this.gameFrame = gameFrame;
    }

    public Player(String name, int id, boolean isAlive, boolean connected) {
        this.name = name;
        this.id = id;
        this.isAlive = isAlive;
        this.connected = connected;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException, ClassNotFoundException {
        Player player = new Player(new GameFrame());

        player.gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closed");
                player.close();
            }
        });

        player.choseNick();

        // Nie wiem jak inaczej zrobic, żeby program czekał, dopóki gracz nie wybierze nicku (to raczej jest do poprawy)
        while(player.getName()==null){
            Thread.sleep(1000);
        }

        player.join();
        player.action();

    }

    public void action() throws InterruptedException, IOException, ClassNotFoundException, ParseException {
        while(this.connected){
            // Read message
            String messageFromServer = (String) this.in.readObject();
            JSONObject jsonMessageFromServer = (JSONObject) this.parser.parse(messageFromServer);
            if(jsonMessageFromServer.get("type").equals("newId")){
                JSONObject playerJsonFromServer = (JSONObject) this.parser.parse((String) jsonMessageFromServer.get("content"));
                this.setId(((Long) playerJsonFromServer.get("id")).intValue());
                System.out.println(this);
            }
            // Write message
            Thread.sleep(2000);
        }
    }

    public void join() throws IOException {
        this.messageToServerJson.put("type", "newPlayer");
        this.messageToServerJson.put("content", this.gson.toJson(this)); // Send player as json
        this.out.writeObject(this.messageToServerJson.toString());
        this.messageToServerJson.keySet().clear();
    }

    // TODO Gracz musi wybrac niezajęty i niepusty nick
    public void choseNick() throws IOException {
        new WindowToChoseNick(this);
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

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

}
