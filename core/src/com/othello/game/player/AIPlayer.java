package com.othello.game.player;

import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;
import java.util.Random;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;

public class AIPlayer extends Player {
    public static final String[] NAMES = {"简单", "普通", "困难"};

    private final int difficulty;

    AIPlayer(int difficulty, int color, OthelloCore core) {
        assert difficulty >= EASY && difficulty <= HARD;
        this.difficulty = difficulty;
        this.setColor(color);
        this.setPlayerID(-1);
        this.setCore(core);
        this.setPlayerName("电脑玩家 - " + NAMES[difficulty]);
    }

    @Override
    public boolean addStep(Step step) {
        return true;
    } // never use this!

    public void addStep() {
        ArrayList<Position> validPosition = getCore().getValidPosition();
        assert validPosition.size() != 0;
        switch(this.difficulty) {
            case EASY:
                getCore().addStep(new Step(validPosition.get(new Random().nextInt(validPosition.size())), this.getColor()));
                return;
            case NORMAL:
                // 逻辑：优先抢角避星，其次选择对手行动力最小的落子方案

                // 抢角
                for(Position position : validPosition)
                    if((position.getX() == 1 || position.getX() == 8) && (position.getY() == 1 || position.getY() == 8)) {
                        getCore().addStep(new Step(position, this.getColor()));
                        return;
                    }

                // 避星
                int starCnt = 0;
                for(Position position : validPosition)
                    if((position.getX() == 1 || position.getX() == 8 || position.getX() == 2 || position.getX() == 7) && (position.getY() == 1 || position.getY() == 8 || position.getY() == 2 || position.getY() == 7))
                        ++starCnt;
                if(starCnt < validPosition.size()) {
                    for(Position position : validPosition)
                        if((position.getX() == 1 || position.getX() == 8 || position.getX() == 2 || position.getX() == 7) && (position.getY() == 1 || position.getY() == 8 || position.getY() == 2 || position.getY() == 7))
                            validPosition.remove(position);
                } // 如果至少存在一个不是星的位置，移除所有星位

                Position bestPosition = validPosition.get(0);
                int minMobility = 65;
                for(Position position : validPosition) {
                    OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                    predictor.addStep(new Step(position, this.getColor()));
                    if(minMobility > predictor.getValidPosition().size()) {
                        minMobility = predictor.getValidPosition().size();
                        bestPosition = position;
                    }
                }
                this.getCore().addStep(new Step(bestPosition, this.getColor()));
        }
    }
}
