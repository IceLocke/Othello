package com.othello.game.player;

import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Step;

public class LocalPlayer extends Player {

    LocalPlayer(int playerID, String playerName, String URL, int playCount, int winCount, OthelloCore core, int color) {
        this.setPlayerID(playerID);
        this.setPlayerName(playerName);
        this.setPlayerProfilePhotoURL(URL);
        this.setPlayCount(playCount);
        this.setWinCount(winCount);
        this.setCore(core);
        this.setColor(color);
    }

    @Override
    public boolean addStep(Step step) {
        return this.getCore().addStep(step);
    }
}
