package com.othello.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class OnlineOthelloServer {
    private String IP = "";
    private int port;
    private ServerSocket server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public OnlineOthelloServer() {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException u) {
            System.out.println("Failed to get Local IP.");
        }

//        port = (int)(Math.random() * 9999 + 10000);
        port = 8080;

        System.out.println("IP = " + IP);
        System.out.println("port = " + port);
        server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
    }

    private boolean connecting = false;
    private boolean connected = false;
    public void connectWithClient() {
        if(connecting) return; // ?
        connecting = true;
        try {
            System.out.println("Try");
            socket = server.accept(new SocketHints());
        } catch(GdxRuntimeException e) {
            System.out.println("Failed");
            connecting = false;
            return;
        }
        connecting = false;
        connected = true;
        System.out.println("Connected successfully.");
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isConnected() {
        return connected;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    private boolean isReceiving = false;
    private boolean isReceived = false;
    private Position lastReceived;
    public Position receive() {
        if(isReceiving) return null;
        else if(isReceived) {
            isReceived = false;
            return lastReceived;
        } else {
            new Thread(new Runnable() {
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
            }).start();
        }
        return null;
    }

    public void update(Step step) {
        try {
            dataOutputStream.writeUTF(String.format("%d %d\n", step.getPosition().getX(), step.getPosition().getY()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}