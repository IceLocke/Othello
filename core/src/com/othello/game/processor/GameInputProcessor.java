package com.othello.game.processor;

import com.badlogic.gdx.InputAdapter;
import com.othello.game.Othello;
import com.othello.game.utils.Position;

public class GameInputProcessor extends InputAdapter {

    public static final int leftUpBoundaryX = 333;
    public static final int leftUpBoundaryY = 28;
    public static final int rightBottomBoundaryX = 995;
    public static final int rightBottomBoundaryY = 680;
    public static final float xShift = (rightBottomBoundaryX - leftUpBoundaryX) / 8.0f;
    public static final float yShift = (rightBottomBoundaryY - leftUpBoundaryY) / 8.0f;

    public Position getPosition(int x, int y) {
        x -= leftUpBoundaryX;
        y -= leftUpBoundaryY;
        return new Position((int)(y / yShift) + 1, (int)(x / xShift) + 1);
    }

    public boolean isInsideBoardBoundary(int x, int y) {
        return x >= leftUpBoundaryX && x <= rightBottomBoundaryX && y >= leftUpBoundaryY && y <= rightBottomBoundaryY;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isInsideBoardBoundary(screenX, screenY)) {
            Othello.boardClicked = true;
            Othello.boardClickPosition = getPosition(screenX, screenY);
        }
        return true;
    }
}
