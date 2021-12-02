package com.othello.game.player;

import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Step;

public class LocalPlayer extends Player {

    public LocalPlayer(int playerID, String playerName, String URL, int playCount, int winCount, OthelloCore core, int color) {
        this.setPlayerID(playerID);
        this.setPlayerName(playerName);
        this.setPlayerProfilePhotoURL(URL);
        this.setPlayCount(playCount);
        this.setWinCount(winCount);
        this.setCore(core);
        this.setColor(color);
    }

    public LocalPlayer(int playerID, String playerName, String URL, int color) {
        this.setPlayerID(playerID);
        this.setPlayerName(playerName);
        this.setPlayerProfilePhotoURL(URL);
        this.setPlayCount(0);
        this.setWinCount(0);
        this.setColor(color);
    }

    @Override
    public void addStep(Step step) {
        this.getCore().addStep(step);
    }

    @Override
    public void addStep() {
    }
}
