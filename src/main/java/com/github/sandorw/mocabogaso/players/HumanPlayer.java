package com.github.sandorw.mocabogaso.players;

import java.util.Scanner;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public final class HumanPlayer<GM extends GameMove, GS extends GameState<GM,? extends GameResult>> implements Player<GM,GS> {
    private final Scanner scanner;

    public HumanPlayer() {
        scanner = new Scanner(System.in, "UTF-8");
    }

    @Override
    public GM chooseMove(GS gameState) {
        System.out.println("Please input a valid move.");
        String input = scanner.next();
        GM newMove = gameState.getMoveFromString(input);
        while (!gameState.isValidMove(newMove)) {
            System.out.println("Invalid move. Try again.");
            input = scanner.next();
            newMove = gameState.getMoveFromString(input);
        }
        return newMove;
    }

    @Override
    public void initialize(GS gameState) {}

    @Override
    public void informOfMove(GM move, GS resultingGameState) {}

    @Override
    public void terminate() {}
}
