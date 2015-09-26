package com.github.sandorw.mocabogaso.players;

import java.util.Scanner;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface for a human player in a game. It uses to keyboard input for move generation.
 *
 * @author sandorw
 */
public final class HumanPlayer implements Player {
    private final Scanner scanner;
    
    public HumanPlayer() {
        scanner = new Scanner(System.in, "UTF-8");
    }

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(
            GS currentGameState) {
        System.out.println("Please input a valid move.");
        String input = scanner.next();
        GM newMove = currentGameState.getMoveFromString(input);
        while ((newMove == null) || !currentGameState.isValidMove(newMove)) {
            System.out.println("Invalid move. Try again.");
            input = scanner.next();
            newMove = currentGameState.getMoveFromString(input);
        }
        return newMove;
    }

    @Override
    public <GM extends GameMove> void informOfMoveMade(GM move) {}

}
