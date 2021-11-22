package com.othello.game.core;

import com.othello.game.utils.Position;
import com.othello.game.utils.Step;
import javafx.geometry.Pos;

import java.util.ArrayList;

public class OthelloCore {
    public final int WHITE = 1;
    public final int BLACK = -1;

    private int[][] board; // 可用区间：(1,1)-(8,8)
    private int turnColor;
    private boolean over;

    public void setBoard(int[][] board) {
        for(int i = 1; i <= 8; ++i)
            for(int j = 1; j <= 8; ++j)
                this.board[i][j] = board[i][j];
    }

    OthelloCore(int turnColor) { // 默认棋盘
        this.turnColor = turnColor;
        board = new int[10][10];
        board[4][4] = board[5][5] = WHITE;
        board[4][5] = board[5][4] = BLACK;
    }

    OthelloCore(int turnColor, int[][] board) {
        this.turnColor = turnColor;
        setBoard(board);
    }

    public int[][] getBoard() {
        return board;
    }

    public int getTurnColor() {
        return turnColor;
    }

    public void reverseColor() {
        turnColor = -turnColor;
    }

    public boolean isValidPosition(Position position, int color) {
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

    ArrayList<Position> getValidPosition(int color) {
        ArrayList<Position> validPosition = new ArrayList<>();
        for(int i = 1; i <= 8; ++i)
            for(int j = 1; j <= 8; ++j)
                if(isValidPosition(new Position(i, j), color))
                    validPosition.add(new Position(i, j));
        return validPosition;
    }

    ArrayList<Position> getValidPosition() { // 默认找当前轮到的颜色的可选位置
        return getValidPosition(turnColor);
    }

    public boolean isOver() {
        return over;
    }

    public boolean addStep(Step A) {
        assert A.getColor() == turnColor;
        if(!isValidPosition(A.getPosition(), A.getColor()))
            return false;
        final int[] dx = {1, 1, 1, 0, 0, -1, -1, -1};
        final int[] dy = {1, 0, -1, 1, -1, 1, 0, -1};
        int x = A.getPosition().getX();
        int y = A.getPosition().getY();
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
        return true;
    }
}
