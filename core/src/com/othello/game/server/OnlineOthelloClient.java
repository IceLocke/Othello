package com.othello.game.server;

import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static com.othello.game.utils.OthelloConstants.DiscType.WHITE;

public class OnlineOthelloClient {
    private int port;
    private String IP;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    public OnlineOthelloClient(String IP, int port) {
        this.IP = IP;
        this.port = port;
        try {
            socket = new Socket(IP, port);
        } catch(IOException e) {
            e.printStackTrace();
        }

        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Step step) {
        try {
            dataOutputStream.writeUTF(String.format("%d %d\n", step.getPosition().getX(), step.getPosition().getY()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String IP = scanner.nextLine();
        int port = scanner.nextInt();
        OnlineOthelloClient client = new OnlineOthelloClient(IP, port);
        while(true) {
            int x, y;
            x = scanner.nextInt();
            y = scanner.nextInt();
            client.update(new Step(new Position(x, y), WHITE));
        }
    }
}
