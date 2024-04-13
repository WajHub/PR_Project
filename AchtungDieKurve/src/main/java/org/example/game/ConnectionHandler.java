package org.example.game;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ConnectionHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream){
        this.socket = socket;
        this.in = objectInputStream;
        this.out = objectOutputStream;
    }


    @Override
    public void run() {
        try{
            System.out.println("Client connected: "+socket.getInetAddress());
            var helloMessage = (String) in.readObject();
            System.out.println(helloMessage);
            out.writeObject("Hello from server");
        } catch (SocketException se) {
            close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            close();
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
