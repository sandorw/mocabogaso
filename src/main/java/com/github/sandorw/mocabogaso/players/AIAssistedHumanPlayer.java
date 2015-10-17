package com.github.sandorw.mocabogaso.players;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface for a human player in a game. It uses to keyboard input for move generation.
 *
 * @author sandorw
 */
public final class AIAssistedHumanPlayer<GM extends GameMove> implements Player<GM> {
    private final Scanner scanner;
    private final AIService<GM> aiService;
    private final ExecutorService executor;
    private volatile boolean dither;
    
    public AIAssistedHumanPlayer(AIService<GM> aiService) {
        scanner = new Scanner(System.in, "UTF-8");
        this.aiService = aiService;
        executor = Executors.newFixedThreadPool(1);
        dither = false;
    }

    @Override
    public <GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(
            GS currentGameState) {
        dither = true;
        executor.submit(new Runnable() {
            public void run() {
                while (dither) {
                    aiService.searchMoves(currentGameState, 100);
                }
            }
        });
        System.out.println("Please input a valid move. Input hint for a suggestion from the AI. Hints will improve over time.");
        GM newMove = null;
        while (true) {
            String input = scanner.next();
            if (input.equals("hint")) {
                System.out.println("Suggested move: " + currentGameState.getHumanReadableMoveString(aiService.selectMove()));
                System.out.println("Please input a valid move. Input hint for a suggestion from the AI.");
            } else {
                newMove = currentGameState.getMoveFromString(input);
                if ((newMove == null) || !currentGameState.isValidMove(newMove)) {
                    System.out.println("Invalid move. Try again.");
                } else {
                    break;
                }
            }
        }
        dither = false;
        return newMove;
    }

    @Override
    public <GS extends GameState<GM, ? extends GameResult>> 
            void informOfMoveMade(GM move, GS resultingGameState) {
        aiService.applyMove(move, resultingGameState);
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }
}
