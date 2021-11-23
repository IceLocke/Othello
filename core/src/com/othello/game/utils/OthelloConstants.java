package com.othello.game.utils;

public interface OthelloConstants {
    interface InterfaceType {
        int HOME = 1;
        int GAME = 2;
        int SINGLE_PLAYER_MENU = 3;
        int MULTIPLE_PLAYER_MENU = 4;
        int ONLINE_MENU = 5;
    }

    interface GameMode {
        int LOCAL_MULTIPLE_PLAYER = 1;
        final int LOCAL_SINGLE_PLAYER = 2;
        final int ONLINE_MULTIPLE_PLAYER = 3;
    }

    interface DiscType {
        int WHITE = 1;
        int BLACK = -1;
        int BLANK = 0;
    }

    interface MenuButtonType {
        int NONE = 0;
        int LOCAL_MULTIPLE_PLAYER = 1;
        int LOCAL_SINGLE_PLAYER = 2;
        int ONLINE_MULTIPLE_PLAYER = 3;
        int EXIT = 4;
    }
}
