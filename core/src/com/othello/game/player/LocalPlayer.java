package com.othello.game.player;

import com.othello.game.utils.Step;
import java.io.Serializable;

public class LocalPlayer extends Player implements Serializable {
    public LocalPlayer(int playerID, String playerName, String URL, int color) {
        this.setPlayerID(playerID);
        this.setPlayerName(playerName);
        this.setPlayerProfilePhotoURL(URL);
        this.setColor(color);
        setLastPlayedBoard(null);
    }

    @Override
    public void addStep(Step step) {
        updateLastPlayedBoard();
        this.getCore().addStep(step);
    }

    @Override
    public void addStep() {
    } // never use this
}
