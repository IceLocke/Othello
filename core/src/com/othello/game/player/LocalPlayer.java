package com.othello.game.player;

import com.othello.game.utils.Step;

public class LocalPlayer extends Player {
    @Override
    public boolean addStep(Step step) {
        return this.getCore().addStep(step);
    }
}
