package com.othello.game.processor;

import com.badlogic.gdx.InputAdapter;
import com.othello.game.Othello;
import com.othello.game.utils.OthelloConstants;

public class HomeInputProcessor extends InputAdapter {
    public int getButtonType(int x, int y) {
        if (x < 100 || x > 420 || y < 420 || y > 680)
            return OthelloConstants.MenuButtonType.NONE;
        if (y < 490)
            return OthelloConstants.MenuButtonType.LOCAL_SINGLE_PLAYER;
        if (y < 560)
            return OthelloConstants.MenuButtonType.LOCAL_MULTIPLE_PLAYER;
        if (y < 620)
            return OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER;
        return OthelloConstants.MenuButtonType.EXIT;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Othello.menuButtonType = getButtonType(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        if (getButtonType(x, y) != 0)
            Othello.menuButtonPressed = true;
        return true;
    }
}
