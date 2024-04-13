package org.example.game.player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlayerHandler implements Runnable{
    private Socket socket;
    private ObjectInputStream out;
    private ObjectOutputStream in;

    public PlayerHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        this.socket = socket;
        this.out = objectInputStream;
        this.in = objectOutputStream;
    }


    @Override
    public void run() {

    }
}
