package com.othello.game.core;

import com.othello.game.utils.Step;

public class LocalOthelloCore extends OthelloCore {

    public boolean addStep(Step step) {
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
        board[x][y] = turnColor;
        reverseColor();
        if(getValidPosition(WHITE).size() == 0 && getValidPosition(BLACK).size() == 0)
            over = true;
        return true;
    }
}
