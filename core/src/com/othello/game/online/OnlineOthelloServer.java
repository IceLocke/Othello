package com.othello.game.online;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnlineOthelloServer extends OnlineOthelloCore {
    private ServerSocket server;

    public OnlineOthelloServer() {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException u) {
            u.printStackTrace();
        }
        port = (int) (Math.random() * 9999 + 10000);
        try {
            server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
        } catch (Exception e) {
            port = (int) (Math.random() * 9999 + 10000);
            server = Gdx.net.newServerSocket(Net.Protocol.TCP, port, new ServerSocketHints());
        }
    }

    private boolean connecting = false;
    private boolean connected = false;

    public void connectWithClient() {
        if (connecting) return; // ?
        connecting = true;
        try {
            socket = server.accept(new SocketHints());
        } catch (GdxRuntimeException e) {
            connecting = false;
            return;
        }
        connecting = false;
        connected = true;
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

    public void sendMaximumRound(int round) {
        try {
            dataOutputStream.writeUTF(String.format("#Round#%d", round));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}