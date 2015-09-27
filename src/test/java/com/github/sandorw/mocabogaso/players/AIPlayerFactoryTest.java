package com.github.sandorw.mocabogaso.players;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Test cases for AIPlayerFactory
 * 
 * @author sandorw
 */
public final class AIPlayerFactoryTest {

    @Test
    public void validAIPlayerTest() {
        SimpleTestGameState gameState = new SimpleTestGameState();
        Game<DefaultGameMove, SimpleTestGameState> game = new Game<>(gameState);
        game.addPlayer("Player 1", AIPlayerFactory.getNewAIPlayer(gameState, 50));
        game.addPlayer("Player 2", AIPlayerFactory.getNewAIPlayer(gameState, 50));
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }

}
