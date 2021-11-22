package com.othello.game.utils;

public class Position {
    private int x;
    private int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
