package com.othello.game.player;

import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class AIPlayer extends Player implements Serializable {
    private static final String[] NAMES = {"Easy", "Normal", "Hard"};
    private static double[] value = new double[10000];
    private static boolean[] vis = new boolean[10000];
    private static boolean haveGotValue = false;

    private final int difficulty;

    private static void DP(int cond) {
        /*
        * 用动态规划求出一条边在某个状态下的胜算有多大
        * 对于一条已经占满的边，认为一个同色棋子得1分，异色棋子扣1分，角落则是得扣2分。
        * 对于一条没有完全占满的边进行记忆化搜索，一般情况下考虑下一步等概率在空格子上落子，
        * 特别地，周围有一个异色棋子时认为落子概率加倍，周围有两个同色棋子时认为落子概率为0。
        * 最后对所有可以转移到的状态求平均分。
        * */
        int condNow = cond; // 状态压缩
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
            value[condNow] = (board[1] + board[8]) << 2;
            for(int i = 2; i <= 7; ++i)
                value[condNow] += board[i];
            return;
        }

        /*
        * 统计得分时，假设自己为白棋（白的值为1）
        * 因此得分就是颜色，如果颜色相反则直接取负（因为得分“正比于”颜色）
        *
        * */

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
        value[condNow] /= W; // 分数对总权重取平均
    }

    private static void getValue() {
        for(int i = 0; i < 6561; ++i) // 6561 = 3^8
            if(!vis[i]) DP(i);
    }

    public AIPlayer(int difficulty, int color) {
        if(!haveGotValue) getValue();
        assert difficulty >= EASY && difficulty <= HARD;
        this.difficulty = difficulty;
        this.setColor(color);
        this.setPlayerID(-1);
        this.setPlayerName("AI-" + NAMES[difficulty]);
        setLastPlayedBoard(null);
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
                OthelloCore predictor = new LocalOthelloCore(core.getTurnColor(), core.getBoard());
                predictor.addStep(new Step(position, predictor.getTurnColor()));
                if(SG(color, predictor)) return true;
            }
            return false;
        } else { // 否则，必须每步都必胜
            for(Position position : validPosition) {
                OthelloCore predictor = new LocalOthelloCore(core.getTurnColor(), core.getBoard());
                predictor.addStep(new Step(position, predictor.getTurnColor()));
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
                cond = cond * 3 + color * board[1][i] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + color * board[8][i] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + color * board[i][1] + 1;
            points += value[cond];
            cond = 0;
            for(int i = 1; i <= 8; ++i)
                cond = cond * 3 + color * board[i][8] + 1;
            points += value[cond];
            return points;
        }
        double points;
        if(now.getTurnColor() == color) {
            points = -1e20;
            for(Position position : now.getValidPosition()) {
                OthelloCore core = new LocalOthelloCore(now.getTurnColor(), now.getBoard());
                core.addStep(new Step(position, color));
                points = max(points, search(color, steps - 1, core.getTurnColor() == -color ? points : 1e20, core));
                if(points > limit) return limit;
                // Alpha-Beta剪枝：已经超过了之前同根状态的最小值，自己取max后再取min一定没有贡献
            }
        } else {
            points = 1e20;
            for(Position position : now.getValidPosition()) {
                OthelloCore core = new LocalOthelloCore(now.getTurnColor(), now.getBoard());
                core.addStep(new Step(position, -color));
                points = min(points, search(color, steps - 1, core.getTurnColor() == color ? points : -1e20, core));
                if(points < limit) return limit;
            }
        }
        return points;
    }

    @Override
    public void addStep(Step step) {} // never use this

    @Override
    public void addStep() {
        if(this.getCore().getTurnColor() != this.getColor()) {
            System.out.println("AIPlayer: Not my turn!");
            return;
        }
        ArrayList<Position> validPosition = getCore().getValidPosition();
        if(validPosition.size() == 0) {
            System.out.println("AIPlayer: Nowhere to put!");
            return;
        }

        updateLastPlayedBoard();
        System.out.println("AIPlayer: AI working now!");

        boolean breakTag = false;
        switch(this.difficulty) {
            case EASY:
                getCore().addStep(new Step(validPosition.get(new Random().nextInt(validPosition.size())), this.getColor()));
                break;

            case NORMAL:
                /*
                * 设计逻辑：（假设自己执黑）
                * 1，步数不少于50时，看做经典有向图游戏求SG函数
                * 2，否则，抢角
                * 3，否则，尝试下在边上，满足下一步不会被对手翻回
                * 4，否则，选令对手行动力最小的一步（优先非星位）
                *
                * 注：该难度为原先设计的困难难度，但发现稍有经验的选手非常容易击败，于是设计了现在基于动态规划和博弈搜索的AI
                *
                * */

                // 1
                if(getStepCnt() >= 50) {
                    for(Position position : validPosition) { // 如果存在必胜策略，只需知道这一步走什么
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        if(SG(this.getColor(), predictor)) { // 这一步必胜
                            this.getCore().addStep(new Step(position, this.getColor()));
                            breakTag = true;
                            break;
                        }
                    }
                }
                if(breakTag) break;

                // 2
                if(strategyAngle(validPosition)) break;

                // 3
                for(Position position : validPosition) {
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
                            breakTag = true;
                        }
                    }
                    if(breakTag) break;
                }
                if(breakTag) break;

                // 4
                strategyNormal(validPosition);
                break;

            case HARD:

                /*
                * 首先，步数不少于50时，暴力出奇迹
                * 否则，如果存在一步可以让自己连走，走这一步
                * 否则，假设对手足够聪明，深搜6层（一般情况下是各下3步），对最坏局面进行评估，计算四条边上自己颜色的得分之和
                * 四条边对占满情况进行估值，其他情况以一定概率进行记忆化搜索转移
                * 否则计算“下一次这条边上落子后期望得分”，记忆化搜索出下一步的状态，去除不可能转移的状态（两侧都为同色不可能落子），对可直接转移的状态提供2倍权重
                * */

                if(getStepCnt() >= 50) {
                    for(Position position : validPosition) { // 如果存在必胜策略，只需知道这一步走什么
                        OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                        if(SG(this.getColor(), predictor)) { // 这一步必胜
                            this.getCore().addStep(new Step(position, this.getColor()));
                            breakTag = true;
                            break;
                        }
                    }
                }
                if(breakTag) break;
                for(Position position : validPosition) {
                    OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                    predictor.addStep(new Step(position, this.getColor()));
                    if(predictor.getTurnColor() == this.getColor()) {
                        this.getCore().addStep(new Step(position, this.getColor()));
                        breakTag = true;
                        break;
                    }
                }
                if(breakTag) break;
                Position best = validPosition.get(0);
                double maxValue = -1e20;
                for(Position position : validPosition) {
                    OthelloCore predictor = new LocalOthelloCore(this.getColor(), this.getCore().getBoard());
                    predictor.addStep(new Step(position, this.getColor()));
                    int steps = 6;
                    // 此时一定不能连走，下一步一定是对手，limit可以直接设置为maxValue
                    double nowValue = search(this.getColor(), steps, maxValue, predictor);
                    if(maxValue < nowValue) {
                        maxValue = nowValue;
                        best = position;
                    }
                }
                System.out.printf("AIPlayer: Finished. point: %f\n", maxValue);
                this.getCore().addStep(new Step(best, this.getColor()));
                break;
        }
        System.out.println("AIPlayer: AI worked out!");
    }

    @Override
    public String toString() {
        return "AIPlayer{" +
                "difficulty=" + difficulty +
                ", playerID=" + playerID +
                ", playerName='" + playerName + '\'' +
                ", playerProfilePhotoURL='" + playerProfilePhotoURL + '\'' +
                ", color=" + color +
                ", lastPlayedBoard=" + Arrays.toString(lastPlayedBoard) +
                '}';
    }
}
