package com.othello.game.player;

import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Step;

public abstract class Player {
    private int playerID;
    private String playerName;
    private String playerProfilePhotoURL;
    private int playCount;
    private int winCount;
    private OthelloCore core;
    private int color;

    public void setCore(OthelloCore core) {
        this.core = core;
    }

    public OthelloCore getCore() {
        return core;
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

    abstract public boolean addStep(Step step);
}