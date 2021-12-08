package com.othello.game.player;

import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;
import java.util.Random;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class AIPlayer extends Player {
    public static final String[] NAMES = {"Easy", "Normal", "Hard"};
    private final int difficulty;

    private static double[] value = new double[10000];
    private static boolean[] vis = new boolean[10000];
    private static boolean haveGotValue = false;

    private static void DP(int cond) {
        int condNow = cond;
        vis[cond] = true;
        int W = 0;
        int[] board = new int[10];
        boolean atLeastOne = false;
        for(int i = 1; i <= 8; ++i) {
            board[i] = cond % 3 - 1;
            cond /= 3;
            if(board[i] == BLANK) atLeastOne = true;
        }
        if(!atLeastOne) {
            if(board[1] == WHITE) value[condNow] += 4;
                else value[condNow] -= 2;
            if(board[8] == WHITE) value[condNow] += 4;
                else value[condNow] -= 2;
            for(int i = 2; i <= 7; ++i)
                if(board[i] == WHITE)
                    value[condNow] += 1;
                else
                    value[condNow] -= 0.3;
            return;
        }
        for(int i = 1; i <= 8; ++i)
            if(board[i] == BLANK) {
                if(!(board[i+1] == WHITE && board[i-1] == WHITE)) {
                    board[i] = WHITE;
                    cond = 0;
                    for(int j = 1; j <= 8; ++j)
                        cond = cond * 3 + board[j] + 1;
                    if(!vis[cond]) DP(cond);
                    ++W;
                    value[condNow] += value[cond];
                    if(board[i+1] == BLACK || board[i-1] == BLACK) {
                        ++W;
                        value[condNow] += value[cond];
                    }
                    board[i] = BLANK;
                }
                if(!(board[i+1] == BLACK && board[i-1] == BLACK)) {
                    board[i] = BLACK;
                    cond = 0;
                    for(int j = 1; j <= 8; ++j)
                        cond = cond * 3 + board[j] + 1;
                    if(!vis[cond]) DP(cond);
                    ++W;
                    value[condNow] += value[cond];
                    if(board[i+1] == WHITE || board[i-1] == WHITE) {
                        ++W;
                        value[condNow] += value[cond];
                    }
                    board[i] = BLANK;
                }
            }
        value[condNow] /= W;
    }

    private static void getValue() {
        for(int i = 0; i < 6561; ++i)
            if(!vis[i]) DP(i);
    }

    public AIPlayer(int difficulty, int color) {
        if(!haveGotValue) getValue();
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

    private double search(int color, int steps, double limit, OthelloCore now) {
        // 评分时默认自己是白棋，如果是黑棋评分恰好为相反数
        if(steps == 0) {
            double points = 0;
            int[][] board = now.getBoard();
            int cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + board[1][i] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + board[8][i] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + board[i][1] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + board[i][8] + 1;
            points += value[cond];
            return color == WHITE ? points : -points;
        }
        double points;
        if(now.getTurnColor() == color) {
            points = -1e20;
            for(Position position : now.getValidPosition()) {
                OthelloCore core = new LocalOthelloCore(now.getTurnColor(), now.getBoard());
                core.addStep(new Step(position, color));
                points = max(points, search(color, steps - 1, limit, core));
                if(points > limit) return limit;
            }
        } else {
            points = 1e20;
            for(Position position : now.getValidPosition()) {
                OthelloCore core = new LocalOthelloCore(now.getTurnColor(), now.getBoard());
                core.addStep(new Step(position, color));
                points = min(points, search(color, steps - 1, points, core));
            }
        }
        return points;
    }

    @Override
    public void addStep(Step step) {}

    @Override
    public void addStep() {
        if(this.getCore().getTurnColor() != this.getColor()) {
            System.out.println("NoNoNo");
        }
        assert this.getCore().getTurnColor() == this.getColor();
        ArrayList<Position> validPosition = getCore().getValidPosition();
        assert validPosition.size() != 0;
        switch(this.difficulty) {
            case EASY:
                getCore().addStep(new Step(validPosition.get(new Random().nextInt(validPosition.size())), this.getColor()));
                return;

            case NORMAL:
                /*
                * 设计逻辑：（假设自己执黑）
                * 1，步数不少于47时，看做经典有向图游戏求SG函数
                * 2，否则，抢角
                * 3，否则，尝试下在边上，满足下一步不会被对手翻回
                * 4，否则，选令对手行动力最小的一步（优先非星位）
                * */

                // 1
                if(getStepCnt() >= 47) {
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

            case HARD:

                /*
                * 首先，步数不少于47时，暴力出奇迹
                * 否则，如果存在一步可以让自己连走，走这一步
                * 否则，假设对手足够聪明，深搜6层（一般情况下是各下3步），对最坏局面进行评估，计算四条边上自己颜色的得分之和
                * 对于一条占满的边，令角得2分，边得1分，乘以颜色（自己1对手-1）
                * 否则计算“下一次这条边上落子后期望得分”，记忆化搜索出下一步的状态，去除不可能转移的状态（两侧都为同色不可能落子），对可直接转移的状态提供3倍权重
                * */

                if(getStepCnt() >= 47) {
                    for(Position position : validPosition) { // 如果存在必胜策略，只需知道这一步走什么
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        if(SG(this.getColor(), predictor)) { // 这一步必胜
                            this.getCore().addStep(new Step(position, this.getColor()));
                            return;
                        }
                    }
                } else {
                    for(Position position : validPosition) {
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        predictor.addStep(new Step(position, this.getColor()));
                        if(predictor.getTurnColor() == this.getColor()) {
                            this.getCore().addStep(new Step(position, this.getColor()));
                            return;
                        }
                    }
                    Position best = validPosition.get(0);
                    double maxValue = -1e20;
                    for(Position position : validPosition) {
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        predictor.addStep(new Step(position, this.getColor()));
                        double nowValue = search(this.getColor(), 4, 1e20, predictor);
                        if(maxValue < nowValue) {
                            maxValue = nowValue;
                            best = position;
                        }
                    }
                    this.getCore().addStep(new Step(best, this.getColor()));
                }
        }
    }

    public static void main(String[] args) {
        new AIPlayer(HARD, WHITE);
        int[] board;
        int cond;

        board = new int[]{0, 1, 1, 1, -1, -1, 1, 0, 0, 0};
        cond = 0;
        for(int i = 1; i <= 8; ++i) {
            cond = cond * 3 + board[i] + 1;
        }
        System.out.println(value[cond]);

        board = new int[]{0, -1, 1, 1, -1, -1, 1, 0, 0, 0};
        cond = 0;
        for(int i = 1; i <= 8; ++i) {
            cond = cond * 3 + board[i] + 1;
        }
        System.out.println(value[cond]);
    }
}
