package com.github.sandorw.mocabogaso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.hex.HexGameState;
import com.github.sandorw.mocabogaso.players.PlayerFactory;
import com.github.sandorw.mocabogaso.players.PlayerDifficulty;

public class Mocabogaso {
    private static Logger LOGGER = LoggerFactory.getLogger(Mocabogaso.class);

    public static void main(String args[]) {
        LOGGER.info("Starting game of Hex");

        String difficultyArg = (args.length > 0 ? args[0] : "");
        PlayerDifficulty difficulty = PlayerDifficulty.fromString(difficultyArg);
        
        HexGameState gameState = HexGameState.of(9);
        Game<DefaultGameMove, HexGameState> game = new Game<>(gameState);
        game.addPlayer("X", PlayerFactory.getNewAIPlayer(gameState, difficulty));
        game.addPlayer("O", PlayerFactory.getNewAIAssistedHumanPlayer(gameState));
        game.playGame();
    }
}
