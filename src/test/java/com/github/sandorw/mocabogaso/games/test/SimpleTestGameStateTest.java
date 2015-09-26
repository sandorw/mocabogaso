package com.github.sandorw.mocabogaso.games.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;

/**
 * Test cases for SimpleTestGameState
 * 
 * @author sandorw
 */
public final class SimpleTestGameStateTest {
    
    private SimpleTestGameState gameState;
    
    @Before
    public void before() {
        gameState = new SimpleTestGameState();
    }

    @Test
    public void copyEqualityTest() {
        SimpleTestGameState copy = (SimpleTestGameState) gameState.getCopy();
        assertEquals(gameState, copy);
        assertEquals(gameState.hashCode(), copy.hashCode());
    }
    
    @Test
    public void getNextPlayerTest() {
        gameState.applyMove(new DefaultGameMove("Player 1", 1));
        assertEquals(gameState.getNextPlayer(), "Player 2");
        gameState.applyMove(new DefaultGameMove("Player 2", 1));
        assertEquals(gameState.getNextPlayer(), "Player 1");
    }
    
    @Test
    public void allPlayerNamesTest() {
        List<String> playerNames = gameState.getAllPlayerNames();
        assertEquals(playerNames.size(), 2);
        assertTrue(playerNames.contains("Player 1"));
        assertTrue(playerNames.contains("Player 2"));
    }
    
    @Test
    public void allValidMovesTest() {
        List<DefaultGameMove> moveList = gameState.getAllValidMoves();
        assertEquals(moveList.size(), 3);
        int moveSum = 0;
        for (DefaultGameMove move : moveList) {
            moveSum += move.getLocation();
            assertEquals(move.getPlayer(), "Player 1");
        }
        assertEquals(moveSum, 6);
    }
    
    @Test
    public void oneValidMoveLeftTest() {
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        gameState.applyMove(new DefaultGameMove("Player 2", 3));
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        List<DefaultGameMove> moveList = gameState.getAllValidMoves();
        assertEquals(moveList.size(), 1);
        DefaultGameMove move = moveList.get(0);
        assertEquals(move.getPlayer(), "Player 2");
        assertEquals(move.getLocation(), 1);
    }
    
    @Test
    public void isValidMoveTest() {
        assertTrue(gameState.isValidMove(new DefaultGameMove("Player 1", 3)));
        assertFalse(gameState.isValidMove(new DefaultGameMove("Player 2", 3)));
    }
    
    @Test
    public void gameOverTest() {
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        gameState.applyMove(new DefaultGameMove("Player 2", 3));
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        gameState.applyMove(new DefaultGameMove("Player 2", 1));
        assertTrue(gameState.isGameOver());
        DefaultGameResult gameResult = gameState.getGameResult();
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "Player 1");
    }
    
    @Test
    public void parseValidMoveTest() {
        DefaultGameMove move = gameState.getMoveFromString("2");
        assertEquals(move.getLocation(), 2);
    }
    
    @Test
    public void parseInvalidMoveTest() {
        DefaultGameMove move = gameState.getMoveFromString("4");
        assertNull(move);
    }
    
    @Test
    public void parseInvalidMoveAtGameEndTest() {
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        gameState.applyMove(new DefaultGameMove("Player 2", 3));
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        DefaultGameMove move = gameState.getMoveFromString("2");
        assertNull(move);
    }

}
