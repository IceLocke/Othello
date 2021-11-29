package com.othello.game.utils;

public interface OthelloConstants {
    interface InterfaceType {
        int HOME = 1;
        int GAME = 2;
        int LOCAL_SINGLE_PLAYER_MENU = 3;
        int LOCAL_MULTIPLE_PLAYER_MENU = 4;
        int ONLINE_MULTIPLE_PLAYER_MENU = 5;
    }

    interface GameMode {
        int LOCAL_MULTIPLE_PLAYER = 1;
        int LOCAL_SINGLE_PLAYER = 2;
        int ONLINE_MULTIPLE_PLAYER = 3;
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

    interface AIDifficulty {
        int EASY = 0;
        int NORMAL = 1;
        int HARD = 2;
    }
}
