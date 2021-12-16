package com.othello.game.core;

import com.othello.game.player.Player;

import java.io.*;

import static com.othello.game.utils.OthelloConstants.DiscType.*;
import static com.othello.game.utils.OthelloConstants.MenuButtonType.*;

public class OthelloGame implements Serializable {

    /*
    * 每一帧扫描：nowPlay是否结束？若结束，创建新游戏；否则继续当前游戏的下一个回合
    */

    private int mode;
    private int roundCount;
    private int maximumPlay;
    private Player player1;
    private Player player2;
    private int player1Score;
    private int player2Score;
    private OthelloCore nowPlay;

    public OthelloGame(Player p1, Player p2, OthelloCore core) {
        this.player1 = p1;
        this.player2 = p2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.nowPlay = core;
        roundCount = 1;
        player1.setCore(core);
        player2.setCore(core);
    }

    public OthelloCore getNowPlay() {
        return nowPlay;
    }

    public void refresh() {
        nowPlay.refresh();
    }

    public void switchToNewGame() {
        if (roundCount < maximumPlay) {
            roundCount++;
            if (nowPlay.getWinner() == BLACK) ++player1Score;
            if (nowPlay.getWinner() == WHITE) ++player2Score;
            refresh();
        }
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getNowPlayer() {
        return nowPlay.getTurnColor() == BLACK ? player1 : player2;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public int[][] getNowPlayBoard() {
        return nowPlay.getBoard();
    }

    public int getMode() {
        return mode;
    }

    public int getMaximumPlay() {
        return maximumPlay;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setMaximumPlay(int maximumPlay) {
        this.maximumPlay = maximumPlay;
    }

    public Player getWinner() {
        int p1Score = player1Score, p2Score = player2Score;
        if(nowPlay.getWinner() == BLACK) p1Score++;
        if(nowPlay.getWinner() == WHITE) p2Score++;
        if (isOver()) {
            if (p1Score > p2Score)
                return player1;
            if (p2Score > p1Score)
                return player2;
        }
        return null;
    }

    public boolean isOver() {
        return roundCount == maximumPlay && nowPlay.isOver();
    }

    public boolean save(int type) {
        File file;
        try {
            FileOutputStream fileOut;
            if(type == LOCAL_SINGLE_PLAYER) {
                file = new File("C://Othello");
                file.mkdir();
                file = new File("C://Othello/LocalSinglePlayerSaver.ser");
                file.createNewFile();
                fileOut = new FileOutputStream("C://Othello/LocalSinglePlayerSaver.ser");
            } else {
                file = new File("C://Othello");
                file.mkdir();
                file = new File("C://Othello/LocalMultiplePlayerSaver.ser");
                file.createNewFile();
                fileOut = new FileOutputStream("C://Othello/LocalMultiplePlayerSaver.ser");
            }
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save.");
            return false;
        }
        return true;
    }

    public static OthelloGame loadGame(int type) {
        OthelloGame game;
        try {
            FileInputStream fileIn;
            if(type == LOCAL_SINGLE_PLAYER)
                fileIn = new FileInputStream("C://Othello/LocalSinglePlayerSaver.ser");
            else
                fileIn = new FileInputStream("C://Othello/LocalMultiplePlayerSaver.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            game = (OthelloGame) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) { // 未找到存档/存档损坏
            i.printStackTrace();
            System.out.println("No an available save.");
            return null;
        }
        game.giveCoreToPlayer();
        return game;
    }

    private void giveCoreToPlayer() {
        player1.setCore(nowPlay);
        player2.setCore(nowPlay);
    }

    public boolean back() {
        if(getNowPlayer().getLastPlayedBoard() == null) {
            System.out.println("Failed to back");
            return false;
        }
        System.out.println("Backed.");
        System.out.println(getNowPlayer());
        nowPlay.setBoard(getNowPlayer().getLastPlayedBoard());
        getNowPlayer().setLastPlayedBoard(null);
        return true;
    }

    @Override
    public String toString() {
        return "OthelloGame{" +
                "mode=" + mode +
                ", roundCount=" + roundCount +
                ", maximumPlay=" + maximumPlay +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", player1Score=" + player1Score +
                ", player2Score=" + player2Score +
                ", nowPlay=" + nowPlay +
                '}';
    }
}
