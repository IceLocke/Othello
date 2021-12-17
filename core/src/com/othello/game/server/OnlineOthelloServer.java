package com.othello.game.server;

import com.badlogic.gdx.Gdx;
import com.othello.game.utils.Position;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class OnlineOthelloServer {
    private String IP = "";
    private int port;
    private ServerSocket server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private boolean isReceiving = false;
    private boolean isReceived = false;
    private Position lastReceived;
    public OnlineOthelloServer() {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException u) {
            System.out.println("Failed to get Local IP.");
        }

        port = (int)(Math.random() * 9999 + 10000);

        System.out.println("IP = " + IP);
        System.out.println("port = " + port);

        try {
            server = new ServerSocket(port);
        } catch(IOException e) {
            System.out.println("Failed to create.");
            e.printStackTrace();
        }
    }

    public void connectWithClient() {
        try {
            socket = server.accept();
            System.out.println("Connected successfully.");
        } catch(IOException e) {
            System.out.println("Failed to connect.");
            e.printStackTrace();
        }
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public Position receive() {
        if(isReceiving) return null;
        else if(isReceived) {
            isReceived = false;
            return lastReceived;
        } else Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                isReceiving = true;
                String string = "";
                try {
                    string = dataInputStream.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scanner scanner = new Scanner(string);
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                System.out.printf("%d %d\n", x, y);
                lastReceived = new Position(x, y);
                isReceiving = false;
                isReceived = true;
            }
        });
        return null;
    }

    public static void main(String[] args) {
        OnlineOthelloServer server;
        server = new OnlineOthelloServer();
        server.connectWithClient();
        while(true) server.receive();
    }
}