package com.othello.game.core;

import com.othello.game.utils.OthelloConstants;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;

public abstract class OthelloCore {
    public final int WHITE = OthelloConstants.DiscType.WHITE;
    public final int BLACK = OthelloConstants.DiscType.BLACK;
    public final int BLANK = OthelloConstants.DiscType.BLANK;

    int[][] board; // 可用区间：(1,1)-(8,8)
    int turnColor;
    boolean over;

    public void setBoard(int[][] board) {
        for(int i = 1; i <= 8; ++i)
            this.board[i] = board[i].clone();
    }

    public void refresh() {
        over = false;
        this.turnColor = BLACK;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++)
                board[i][j] = BLANK;
        }
        board[4][4] = board[5][5] = WHITE;
        board[4][5] = board[5][4] = BLACK;
    }

    public OthelloCore() {
        board = new int[10][10];
        refresh();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getTurnColor() {
        if(getValidPosition(turnColor).size() == 0)
            reverseColor();
        if(getValidPosition(turnColor).size() == 0)
            over = true;
        return turnColor;
    }

    public void reverseColor() {
        turnColor = -turnColor;
    }

    public boolean isValidPosition(Position position, int color) {
        if(this.getBoard()[position.getX()][position.getY()] != BLANK)
            return false;
        final int[] dx = {1, 1, 1, 0, 0, -1, -1, -1};
        final int[] dy = {1, 0, -1, 1, -1, 1, 0, -1};
        int x = position.getX();
        int y = position.getY();
        for(int d = 0; d < 8; ++d) {
            // 判断逻辑：往一个方向先走一步（确定至少能翻子），然后走到底（全翻过来），判断停止时是否停在一个同样颜色的棋子上
            int tx = x + dx[d], ty = y + dy[d];
            if(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] + color == 0) {
                while(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] + color == 0) {
                    tx += dx[d];
                    ty += dy[d];
                }
                if(tx >= 1 && tx <= 8 && ty >= 1 && ty <= 8 && getBoard()[tx][ty] == color)
                    return true;
            }
        }
        return false;
    }

    public ArrayList<Position> getValidPosition(int color) {
        ArrayList<Position> validPosition = new ArrayList<>();
        for(int i = 1; i <= 8; ++i)
            for(int j = 1; j <= 8; ++j)
                if(isValidPosition(new Position(i, j), color))
                    validPosition.add(new Position(i, j));
        return validPosition;
    }

    public ArrayList<Position> getValidPosition() { // 默认找当前轮到的颜色的可选位置
        return getValidPosition(turnColor);
    }

    public abstract boolean addStep(Step step);

    public boolean isOver() {
        if(getValidPosition(BLACK).size() == 0 && getValidPosition(WHITE).size() == 0)
            over = true;
        return over;
    }

    public int getWinner() {
        if(!over) return 0;
        int whitePoints = 0, blackPoints = 0;
        for(int i = 1; i <= 8; ++i)
            for(int j = 1; j <= 8; ++j)
                if(board[i][j] == WHITE)
                    ++whitePoints;
                else if(board[i][j] == BLACK)
                    ++blackPoints;
        if(whitePoints == blackPoints) return BLANK;
        return whitePoints > blackPoints ? WHITE : BLACK;
    }
}
