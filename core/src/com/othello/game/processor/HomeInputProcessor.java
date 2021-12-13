package com.othello.game.processor;

import com.badlogic.gdx.InputAdapter;
import com.othello.game.Othello;
import com.othello.game.utils.OthelloConstants;

public class HomeInputProcessor extends InputAdapter {
    public int getButtonType(int x, int y) {
        if (x < 100 || x > 360 || y < 420 || y > 660)
            return OthelloConstants.MenuButtonType.NONE;
        if (y < 470)
            return OthelloConstants.MenuButtonType.LOCAL_SINGLE_PLAYER;
        if (y < 530)
            return OthelloConstants.MenuButtonType.LOCAL_MULTIPLE_PLAYER;
        if (y < 600)
            return OthelloConstants.MenuButtonType.ONLINE_MULTIPLE_PLAYER;
        return OthelloConstants.MenuButtonType.EXIT;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (Othello.interfaceType != OthelloConstants.InterfaceType.HOME)
            return true;
        Othello.menuButtonType = getButtonType(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) {
        if (Othello.interfaceType != OthelloConstants.InterfaceType.HOME)
            return true;
        if (getButtonType(x, y) != 0)
            Othello.menuButtonPressed = true;
        return true;
    }
}
