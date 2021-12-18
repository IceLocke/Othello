package com.othello.game.player;

import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Step;

import java.io.Serializable;
import java.util.Arrays;

import static com.othello.game.utils.OthelloConstants.DiscType.*;

public abstract class Player implements Serializable {
    protected int playerID;
    protected String playerName;
    protected String playerProfilePhotoURL = "";
    protected int color;
    protected int[][] lastPlayedBoard;
    private transient OthelloCore core;

    public boolean check() {
        return color == WHITE || color == BLACK;
    }

    public void setCore(OthelloCore core) {
        this.core = core;
    }

    public OthelloCore getCore() {
        return core;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerProfilePhotoURL(String playerProfilePhotoURL) {
        this.playerProfilePhotoURL = playerProfilePhotoURL;
    }

    public int getID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerProfilePhotoURL() {
        return playerProfilePhotoURL;
    }

    public int getColor() {
        return color;
    }

    public int[][] getLastPlayedBoard() {
        return lastPlayedBoard;
    }

    public void setLastPlayedBoard(int[][] lastPlayedBoard) {
        this.lastPlayedBoard = lastPlayedBoard;
    }

    public void updateLastPlayedBoard() {
        lastPlayedBoard = new int[10][10];
        for(int i = 1; i <= 8; ++i)
            lastPlayedBoard[i] = core.board[i].clone();
    }

    abstract public void addStep();
    abstract public void addStep(Step step);

    @Override
    public String toString() {
        return "Player{" +
                "playerID=" + playerID +
                ", playerName='" + playerName + '\'' +
                ", playerProfilePhotoURL='" + playerProfilePhotoURL + '\'' +
                ", color=" + color +
                ", lastPlayedBoard=" + Arrays.toString(lastPlayedBoard) +
                ", core=" + core +
                '}';
    }
}