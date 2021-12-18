package com.othello.game.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.othello.game.Othello;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class OnlineOthelloClient {
    private int port;
    private String IP;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    public OnlineOthelloClient(String IP, int port) {
        this.IP = IP;
        this.port = port;
    }

    public void connectWithServer() {
        try {
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, IP, port, new SocketHints());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch(NullPointerException | GdxRuntimeException e) {
            socket = null;
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null;
    }

    private boolean isReceiving = false;
    private boolean isReceived = false;
    private Position lastReceived;
    private String remoteName = null;
    private int maximumRound = 0;

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

                    if (string.contains("#Round#")) {
                        System.out.println("receive name");
                        maximumRound = Integer.parseInt(string.split("#Round#")[1]);
                        isReceiving = false;
                        isReceived = true;
                    }

                    if (string.contains("Disconnect")) {
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

    public int getMaximumRound() {
        return maximumRound;
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