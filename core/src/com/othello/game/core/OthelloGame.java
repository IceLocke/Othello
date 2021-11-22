package com.othello.game.core;

import com.othello.game.player.Player;

import java.io.File;
import java.util.ArrayList;

public class OthelloGame {
    private static int gameCount = 0;
    private int gameID;
    private String gameName;
    /*
    * We use numbers to simplify the game mode:
    * 1 - Local multiple players
    * 2 - Local single player
    * 3 - Online multiple players
    */
    private int mode;
    private ArrayList<OthelloPlay> playList;
    private int playCount;
    private int maximumPlay;
    private Player player1;
    private Player player2;
    private int player1Score;
    private int player2Score;
    private boolean over;

    OthelloGame(File save) {

    }

    OthelloGame(Player p1, Player p2) {
        this.gameID = ++OthelloGame.gameCount;
        this.gameName = String.format("Game%d", this.gameID);
        this.player1 = p1;
        this.player2 = p2;
        this.player1Score = 0;
        this.player2Score = 0;
    }

    OthelloGame(Player p1, Player p2, String name) {
        this.gameID = ++OthelloGame.gameCount;
        this.gameName = name;
        this.player1 = p1;
        this.player2 = p2;
        this.player1Score = 0;
        this.player2Score = 0;
    }

    public int getGameID() {
        return this.gameID;
    }

    public String getGameName() {
        return this.gameName;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public ArrayList<OthelloPlay> getPlayList() {
        return this.playList;
    }

    public OthelloPlay getPlayByID(int id) throws Exception {
        OthelloPlay play = null;
        for (OthelloPlay p : playList) {
            if (p.getPlayID() == id) {
                play = p;
                break;
            }
        }
        if (play != null)
            return play;
        else
            throw new Exception(String.format("Cannot find play id: %d", id));
    }

}