package com.othello.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.othello.game.player.Player;
import com.othello.game.utils.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.FileHandler;

import static com.othello.game.utils.OthelloConstants.DiscType.*;

public class OthelloGame {

    /*
    * 每一帧扫描：nowPlay是否结束？若结束，创建新游戏；否则继续当前游戏的下一个回合
    */

    private int mode;
    private int roundCount;
    private int maximumPlay;
    private Player player1;
    private Player player2;
    private int player1Score;
    private int player2Score;
    private OthelloCore nowPlay;

    public OthelloGame(FileHandle file) {

    }

    public OthelloGame(Player p1, Player p2, OthelloCore core) {
        this.player1 = p1;
        this.player2 = p2;
        this.player1Score = 0;
        this.player2Score = 0;
        this.nowPlay = core;
        roundCount = 1;
        player1.setCore(core);
        player2.setCore(core);
    }

    public OthelloCore getNowPlay() {
        return nowPlay;
    }

    public void refresh() {
        nowPlay.refresh();
    }

    public void switchToNewGame() {
        if (roundCount < maximumPlay) {
            roundCount++;
            if (nowPlay.getWinner() == BLACK) ++player1Score;
            if (nowPlay.getWinner() == WHITE) ++player2Score;
            refresh();
        }
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getNowPlayer() {
        return nowPlay.getTurnColor() == BLACK ? player1 : player2;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public int[][] getNowPlayBoard() {
        return nowPlay.getBoard();
    }

    public int getMode() {
        return mode;
    }

    public int getMaximumPlay() {
        return maximumPlay;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setMaximumPlay(int maximumPlay) {
        this.maximumPlay = maximumPlay;
    }

    public void save() {

    }

    public Player getWinner() {
        int p1Score = player1Score, p2Score = player2Score;
        if(nowPlay.getWinner() == BLACK) p1Score++;
        if(nowPlay.getWinner() == WHITE) p2Score++;
        if (isOver()) {
            if (p1Score > p2Score)
                return player1;
            if (p2Score > p1Score)
                return player2;
        }
        return null;
    }

    public boolean isOver() {
        return roundCount == maximumPlay && nowPlay.isOver();
    }

}
