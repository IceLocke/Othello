package com.othello.game.player;

import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;
import java.util.Random;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.BLANK;

public class AIPlayer extends Player {
    public static final String[] NAMES = {"Easy", "Normal", "Hard"};

    private final int difficulty;

    public AIPlayer(int difficulty, int color, OthelloCore core) {
        assert difficulty >= EASY && difficulty <= HARD;
        this.difficulty = difficulty;
        this.setColor(color);
        this.setPlayerID(-1);
        this.setCore(core);
        this.setPlayerName("AI - " + NAMES[difficulty]);
    }

    public AIPlayer(int difficulty, int color) {
        assert difficulty >= EASY && difficulty <= HARD;
        this.difficulty = difficulty;
        this.setColor(color);
        this.setPlayerID(-1);
        this.setPlayerName("NPC - " + NAMES[difficulty]);
    }

    private int getStepCnt() {
        int cnt = 0;
        for(int i = 1; i <= 8; ++i)
            for(int j = 1; j <= 8; ++j)
                if(this.getCore().getBoard()[i][j] != BLANK)
                    ++cnt;
        return cnt;
    }

    private boolean SG(int color, OthelloCore core) { // 和传统SG函数不同，直接返回是否为选中的颜色获胜
        if(core.isOver())
            return core.getWinner() == color;
        ArrayList<Position> validPosition = core.getValidPosition();
        assert validPosition.size() != 0;
        if(core.getTurnColor() == color) { // 如果当前是自己在走，存在必胜决策即可
            for(Position position : validPosition) {
                OthelloCore predictor = new LocalOthelloCore(color, core.getBoard());
                predictor.addStep(new Step(position, color));
                if(SG(color, predictor)) return true;
            }
            return false;
        } else { // 否则，必须每步都必胜
            for(Position position : validPosition) {
                OthelloCore predictor = new LocalOthelloCore(color, core.getBoard());
                predictor.addStep(new Step(position, color));
                if(!SG(color, predictor)) return false;
            }
            return true;
        }
    }

    private boolean strategyAngle(ArrayList<Position> validPosition) {
        for(Position position : validPosition)
            if((position.getX() == 1 || position.getX() == 8) && (position.getY() == 1 || position.getY() == 8)) {
                getCore().addStep(new Step(position, this.getColor()));
                return true;
            }
        return false;
    }

    private void strategyNormal(ArrayList<Position> validPosition) {
        int starCnt = 0;
        for(Position position : validPosition)
            if((position.getX() == 1 || position.getX() == 8 || position.getX() == 2 || position.getX() == 7) && (position.getY() == 1 || position.getY() == 8 || position.getY() == 2 || position.getY() == 7))
                ++starCnt;
        boolean removable = starCnt < validPosition.size();
        Position bestPosition = validPosition.get(0);
        int minMobility = 65;
        for(Position position : validPosition) {
            if((position.getX() == 1 || position.getX() == 8 || position.getX() == 2 || position.getX() == 7) && (position.getY() == 1 || position.getY() == 8 || position.getY() == 2 || position.getY() == 7))
                if(removable) continue;
            OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
            predictor.addStep(new Step(position, this.getColor()));
            if(minMobility > predictor.getValidPosition().size()) {
                minMobility = predictor.getValidPosition().size();
                bestPosition = position;
            }
        }
        this.getCore().addStep(new Step(bestPosition, this.getColor()));

    }

    @Override
    public void addStep() {
        ArrayList<Position> validPosition = getCore().getValidPosition();
        assert validPosition.size() != 0;
        switch(this.difficulty) {
            case EASY:
                getCore().addStep(new Step(validPosition.get(new Random().nextInt(validPosition.size())), this.getColor()));
                return;

            case NORMAL:
                if(!strategyAngle(validPosition))
                    strategyNormal(validPosition);
                return;

            case HARD:
                /*
                * 设计逻辑：（假设自己执黑）
                * 1，步数不少于50时，看做经典有向图游戏求SG函数
                * 2，否则，抢角
                * 3，否则，尝试下在边上，满足下一步不会被对手翻回
                * 4，否则，选令对手行动力最小的一步（优先非星位）
                * */

                // 1
                if(getStepCnt() >= 45) {
                    for(Position position : validPosition) { // 如果存在必胜策略，只需知道这一步走什么
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        if(SG(this.getColor(), predictor)) { // 这一步必胜
                            this.getCore().addStep(new Step(position, this.getColor()));
                            return;
                        }
                    }
                }

                // 2
                if(strategyAngle(validPosition)) return;

                // 3
                for(Position position : validPosition)
                    if(position.getX() == 1 || position.getX() == 8 || position.getY() == 1 || position.getY() == 8) {
                        if(position.getX() == 2 || position.getX() == 7 || position.getY() == 2 || position.getY() == 7)
                            continue;
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        predictor.addStep(new Step(position, this.getColor()));
                        ArrayList<Position> nextValidPosition = predictor.getValidPosition();
                        boolean tag = true;
                        // 假设自己走了这一步，考虑对手所有可走的位置，如果存在一种方案将自己翻回则不选
                        for(Position nextPosition : nextValidPosition) {
                            OthelloCore newPredictor = new LocalOthelloCore(predictor.getTurnColor(), predictor.getBoard());
                            newPredictor.addStep(new Step(nextPosition, newPredictor.getTurnColor()));
                            if(newPredictor.getBoard()[position.getX()][position.getY()] != this.getColor()) {
                                tag = false;
                                break;
                            }
                        }
                        if(tag) {
                            getCore().addStep(new Step(position, this.getColor()));
                            return;
                        }
                    }

                // 4
                strategyNormal(validPosition);
        }
    }
}
