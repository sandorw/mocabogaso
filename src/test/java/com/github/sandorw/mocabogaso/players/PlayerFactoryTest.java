package com.github.sandorw.mocabogaso.players;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Test cases for PlayerFactory
 * 
 * @author sandorw
 */
public final class PlayerFactoryTest {
    SimpleTestGameState gameState;
    Game<DefaultGameMove, SimpleTestGameState> game;
    
    @Before
    public void before() {
        gameState = new SimpleTestGameState();
        game = new Game<>(gameState);
    }
    
    @Test
    public void defaultAIPlayerTest() {
        game.addPlayer("Player 1", PlayerFactory.getNewAIPlayer(gameState, 50));
        game.addPlayer("Player 2", PlayerFactory.getNewAIPlayer(gameState, 50));
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }
    
    @Test
    public void amafAIPlayerTest() {
        game.addPlayer("Player 1", PlayerFactory.getNewAMAFAIPlayer(gameState, 50));
        game.addPlayer("Player 2", PlayerFactory.getNewAMAFAIPlayer(gameState, 50));
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }
    
    @Test
    public void multiThreadedAMAFAIPlayerTest() {
        game.addPlayer("Player 1", PlayerFactory.getNewMultiThreadedAMAFAIPlayer(gameState, 50, 2));
        game.addPlayer("Player 2", PlayerFactory.getNewMultiThreadedAMAFAIPlayer(gameState, 50, 2));
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }
    
    @Test
    public void multiThreadedUnsafeAMAAIPlayerTest() {
        game.addPlayer("Player 1", PlayerFactory.getNewUnsafeMultiThreadedAMAFAIPlayer(gameState, 50, 2));
        game.addPlayer("Player 2", PlayerFactory.getNewUnsafeMultiThreadedAMAFAIPlayer(gameState, 50, 2));
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }
}
