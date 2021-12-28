package com.othello.game.online;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class OnlineOthelloClient extends OnlineOthelloCore {
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
}