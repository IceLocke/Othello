package com.othello.game.core;

import com.othello.game.player.Player;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;

import static com.othello.game.utils.OthelloConstants.DiscType.BLACK;
import static com.othello.game.utils.OthelloConstants.DiscType.WHITE;

public class OthelloPlay {
    private static int playCnt = 0;
    private final int playID;
    private final String playName;
    private final OthelloCore core;
    ArrayList<Step> stepList;
    private int stepCount;
    Player whitePlayer;
    Player blackPlayer;

    public OthelloPlay(Player whitePlayer, Player blackPlayer, OthelloCore core, String name) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.core = core;
        this.playID = ++playCnt;
        this.playName = name;
    }

    public OthelloPlay(Player whitePlayer, Player blackPlayer, OthelloCore core) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.core = core;
        this.playID = ++playCnt;
        this.playName = String.format("GAME %d", this.playID);
    }

    public int getPlayID() {
        return playID;
    }

    public String getPlayName() {
        return playName;
    }

    public ArrayList<Step> getStepList() {
        return stepList;
    }

    public int[][] getBoard() {
        return core.getBoard();
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Player getWinner() {
        switch(core.getWinner()) {
            case WHITE: return whitePlayer;
            case BLACK: return blackPlayer;
            default: return null; // BLANK
        }
    }

    public Player getTurnPlayer() {
        return core.getTurnColor() == WHITE ? whitePlayer : blackPlayer;
    }

    public boolean isOver() {
        return core.isOver();
    }

    public ArrayList<Position> getValidPosition() {
        return core.getValidPosition();
    }
}
