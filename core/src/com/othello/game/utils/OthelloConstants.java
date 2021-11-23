package com.othello.game.utils;

public interface OthelloConstants {
    public interface InterfaceType {
        final int HOME = 1;
        final int GAME = 2;
        final int SINGLE_PLAYER_MENU = 3;
        final int MULTIPLE_PLAYER_MENU = 4;
        final int ONLINE_MENU = 5;
    }

    public interface GameMode {
        final int LOCAL_MULTIPLE_PLAYER = 1;
        final int LOCAL_SINGLE_PLAYER = 2;
        final int ONLINE_MULTIPLE_PLAYER = 3;
    }

    public interface DiscType {
        final int WHITE = 1;
        final int BLACK = -1;
        final int BLANK = 0;
    }

    public interface MenuButtonType {
        final int NONE = 0;
        final int LOCAL_MULTIPLE_PLAYER = 1;
        final int LOCAL_SINGLE_PLAYER = 2;
        final int ONLINE_MULTIPLE_PLAYER = 3;
        final int EXIT = 4;
    }
}
