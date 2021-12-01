package com.othello.game.player;

import com.othello.game.core.LocalOthelloCore;
import com.othello.game.core.OthelloCore;
import com.othello.game.utils.Position;
import com.othello.game.utils.Step;

import java.util.ArrayList;
import java.util.Scanner;

import static com.othello.game.utils.OthelloConstants.AIDifficulty.*;
import static com.othello.game.utils.OthelloConstants.DiscType.*;

public class AIPlayerTester {
    static public void main(String[] args) {
        final char[] ch = {'1', ' ', '0'};
        OthelloCore core = new LocalOthelloCore();
        Player AI = new AIPlayer(HARD, WHITE);
        AI.setCore(core);
        while(true) {
            System.out.println("+---+---+---+---+---+---+---+---+");
            for(int i = 1; i <= 8; ++i) {
                for(int j = 1; j <= 8; ++j)
                    System.out.printf("| %s ", ch[core.getBoard()[i][j]+1]);
                System.out.println("|");
                System.out.println("+---+---+---+---+---+---+---+---+");
            }
            System.out.println();
            if(core.isOver()) break;
            if(core.getTurnColor() == WHITE)
                AI.addStep();
            else {
                int x, y;
                ArrayList<Position> validPosition = core.getValidPosition();
                for(Position position : validPosition)
                    System.out.printf("|%d %d| ", position.getX(), position.getY());
                System.out.println();
                Scanner scanner = new Scanner(System.in);
                x = scanner.nextInt();
                y = scanner.nextInt();
                if(!core.getValidPosition().contains(new Position(x, y)))
                    System.out.println("Seriously???");
                else
                    core.addStep(new Step(new Position(x, y), BLACK));
            }
        }
        System.out.printf("GG Winner: %s\n", core.getWinner() == WHITE ? "AI" : (core.getWinner() == BLACK ? "Player" : "Nobody"));
    }
}
