package com.othello.game.core;

import com.othello.game.utils.Step;

public class LocalOthelloCore extends OthelloCore {
    public LocalOthelloCore(int turnColor, int[][] board) {
        this.turnColor = turnColor;
        setBoard(board);
    }

    public LocalOthelloCore() {
        this.turnColor = BLACK;
        board = new int[10][10];
        board[4][4] = board[5][5] = WHITE;
        board[4][5] = board[5][4] = BLACK;
    }

    public boolean addStep(Step step) {
        if(step.getColor() != super.turnColor) {
            System.out.println("LocalOthelloCore: WHO???");
        }
        assert step.getColor() == super.turnColor;
        if(!isValidPosition(step.getPosition(), step.getColor()))
            return false;
        final int[] dx = {1, 1, 1, 0, 0, -1, -1, -1};
        final int[] dy = {1, 0, -1, 1, -1, 1, 0, -1};
        int x = step.getPosition().getX();
        int y = step.getPosition().getY();
        for(int d = 0; d < 8; ++d) {
            int tx = x + dx[d], ty = y + dy[d];
            if(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] + turnColor == 0) {
                while(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] + turnColor == 0) {
                    tx += dx[d];
                    ty += dy[d];
                }
                if(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] == turnColor)
                    while(tx != x || ty != y) {
                        board[tx][ty] = turnColor;
                        tx -= dx[d];
                        ty -= dy[d];
                    }
            }
        }
        board[x][y] = super.turnColor;
        if(getValidPosition(WHITE).size() == 0 && getValidPosition(BLACK).size() == 0)
            super.over = true;
        else if(getValidPosition(-turnColor).size() != 0) // 注意！只有在可行时换人下
            reverseColor();
        else {
            System.out.println("LocalOthelloCore: Once More!");
            // 提示该玩家再走一步，但应该不是core来做
        }
        return true;
    }
}
