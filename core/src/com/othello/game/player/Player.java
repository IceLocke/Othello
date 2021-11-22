package com.othello.game.player;

import com.othello.game.core.OthelloCore;

public abstract class Player {
    private int playerID;
    private String playerName;
    private String playerProfilePhotoURL;
    private int playCount;
    private int winCount;
    private OthelloCore core;
    private int color;

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

    public int getPlayCount() {
        return playCount;
    }

    public int getWinCount() {
        return winCount;
    }

    public void addPlayCount() {
        ++playCount;
    }

    public void addWinCount() {
        ++winCount;
    }

    abstract public boolean addStep();
}