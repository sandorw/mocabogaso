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
public final class HumanPlayer<GM extends GameMove> implements Player<GM> {
    private final Scanner scanner;
    
    public HumanPlayer() {
        scanner = new Scanner(System.in, "UTF-8");
    }

    @Override
    public <GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(
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
    public <GS extends GameState<GM, ? extends GameResult>> 
        void informOfMoveMade(GM move, GS resultingGameState) {}

}
