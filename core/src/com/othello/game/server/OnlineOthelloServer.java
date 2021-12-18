package com.othello.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.othello.game.Othello;
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
        } catch (UnknownHostException u) {
            System.out.println("Failed to get Local IP.");
        }

        port = (int)(Math.random() * 9999 + 10000);

        System.out.println("IP = " + IP);
        System.out.println("port = " + port);
        try {
            server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
        } catch (Exception e) {
            port = (int)(Math.random() * 9999 + 10000);
            server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
        }
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
    private String remoteName = null;

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
                    System.out.println(string);
                    if (string.contains("#Step#")) {
                        System.out.println("receive step");
                        Scanner scanner = new Scanner(string.split("#Step#")[1]);
                        int x = scanner.nextInt();
                        int y = scanner.nextInt();
                        System.out.printf("%d %d\n", x, y);
                        lastReceived = new Position(x, y);
                        isReceiving = false;
                        isReceived = true;
                    }
                    if (string.contains("#Name#")) {
                        System.out.println("receive name");
                        remoteName = string.split("#Name#")[1];
                        isReceiving = false;
                        isReceived = true;
                    }

                    if (string.contains("Disconnect")) {
                        System.out.println("disconnect");
                        Othello.remotePlayerDisconnected = true;
                        isReceiving = false;
                        isReceived = true;
                    }
                }
            }).start();
        }
        return null;
    }

    public void update(Step step) {
        try {
            dataOutputStream.writeUTF(String.format("#Step#%d %d\n", step.getPosition().getX(), step.getPosition().getY()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPlayerName(String name) {
        try {
            dataOutputStream.writeUTF(String.format("#Name#%s", name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMaximumRound(int round) {
        try {
            dataOutputStream.writeUTF(String.format("#Round#%d", round));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void disconnect() {
        try {
            dataOutputStream.writeUTF("Disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}