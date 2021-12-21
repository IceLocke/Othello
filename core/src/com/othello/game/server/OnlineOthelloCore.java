package com.othello.game.server;

import com.badlogic.gdx.net.Socket;
import com.othello.game.Othello;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class OnlineOthelloCore {
    protected int port;
    protected String IP;
    protected Socket socket;
    protected DataOutputStream dataOutputStream;
    protected DataInputStream dataInputStream;

    public OnlineOthelloCore() {
    }

    public boolean isConnected() {
        return socket != null;
    }

    protected boolean isReceiving = false;
    protected boolean isReceived = false;
    protected Position lastReceived;
    protected String remoteName = null;
    protected int maximumRound = 0;

    public Position receive() {
        if (isReceiving) return null;
        else if (isReceived) {
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
                        Scanner scanner = new Scanner(string.split("#Step#")[1]);
                        int x = scanner.nextInt();
                        int y = scanner.nextInt();
                        lastReceived = new Position(x, y);
                        isReceiving = false;
                        isReceived = true;
                    }

                    if (string.contains("#Name#")) {
                        remoteName = string.split("#Name#")[1];
                        isReceiving = false;
                        isReceived = true;
                    }

                    if (string.contains("#Round#")) {
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
            if (dataOutputStream != null)
                dataOutputStream.writeUTF("Disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}