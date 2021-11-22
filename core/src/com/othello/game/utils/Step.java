package com.othello.game.utils;

public class Step {
    private int color;
    private Position position;

    public Step(int x, int y, int color) {
        position = new Position(x, y);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }
}
