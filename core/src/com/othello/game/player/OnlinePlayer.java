package com.othello.game.player;

import com.othello.game.server.OnlineOthelloClient;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

public class OnlinePlayer extends Player {
    OnlineOthelloClient client;
    public OnlinePlayer(int playerID, String playerName, String URL, int color) {
        this.playerID = playerID;
        this.color = color;
        this.playerName = playerName;
        this.lastPlayedBoard = null;
        this.playerProfilePhotoURL = URL;
    }

    public void setClient(OnlineOthelloClient client) {
        this.client = client;
    }

    @Override
    public void addStep() {

    }
    @Override
    public void addStep(Step step) {}
}
