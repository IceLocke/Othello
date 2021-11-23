package com.othello.game.utils;

import java.util.ArrayList;

public class DiscList {
    private ArrayList<Disc> discList;

    public DiscList() {
        discList = new ArrayList<>();
    }

    public boolean discAtPositionExist(int x, int y) {
        for (Disc disc : discList) {
            if (disc.getX() == x && disc.getY() == y)
                return true;
        }
        return false;
    }

    public Disc getDiscAtPosition(int x, int y) {
        Disc disc = null;
        for (Disc d : discList) {
            if (d.getX() == x && d.getY() == y)
                disc = d;
        }
        return disc;
    }

    public boolean addDisc(Disc disc) {
        if (disc != null) {
            discList.add(disc);
            return true;
        }
        else return false;
    }

    public ArrayList<Disc> getDiscList() {
        return discList;
    }

    public int getDiscListSize() {
        return discList.size();
    }

    public Disc getLastDisc() {
        Disc disc = null;
        if (discList.size() > 0)
            disc = discList.get(discList.size() - 1);
        return disc;
    }
}
