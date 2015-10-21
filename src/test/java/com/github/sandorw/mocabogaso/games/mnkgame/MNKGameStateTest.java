package com.github.sandorw.mocabogaso.games.mnkgame;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.players.PlayerFactory;

/**
 * Test cases for MNKGameState
 * 
 * @author sandorw
 */
public final class MNKGameStateTest {
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void negativeMExceptionTest() {
        exception.expect(IllegalArgumentException.class);
        MNKGameState.of(-1, 1, 1);
    }

    @Test
    public void negativeNExceptionTest() {
        exception.expect(IllegalArgumentException.class);
        MNKGameState.of(1, -1, 1);
    }
    
    @Test
    public void negativeKExceptionTest() {
        exception.expect(IllegalArgumentException.class);
        MNKGameState.of(1, 1, -1);
    }
    
    @Test
    public void kGreaterThanMNExceptionTest() {
        exception.expect(IllegalArgumentException.class);
        MNKGameState.of(1, 1, 2);
    }
    
    @Test
    public void copyEqualityTest() {
        MNKGameState gameState = MNKGameState.of(3, 4, 3);
        gameState.applyMove(new DefaultGameMove("X",3));
        gameState.applyMove(new DefaultGameMove("O",11));
        gameState.applyMove(new DefaultGameMove("X",5));
        MNKGameState copy = (MNKGameState) gameState.getCopy();
        assertTrue(gameState.equals(copy));
    }
    
    @Test
    public void nextPlayerUpdateTest() {
        MNKGameState gameState = MNKGameState.of(3, 4, 3);
        assertEquals(gameState.getNextPlayerName(), "X");
        gameState.applyMove(new DefaultGameMove("X",3));
        assertEquals(gameState.getNextPlayerName(), "O");
    }
    
    @Test
    public void getAllPlayerNamesTest() {
        MNKGameState gameState = MNKGameState.of(3, 4, 3);
        List<String> playerNames = gameState.getAllPlayerNames();
        assertEquals(playerNames.size(), 2);
        assertTrue(playerNames.contains("X") && playerNames.contains("O"));
    }
    
    @Test
    public void getAllValidMovesTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        gameState.applyMove(new DefaultGameMove("X",3));
        gameState.applyMove(new DefaultGameMove("O",0));
        List<DefaultGameMove> validMoves = gameState.getAllValidMoves();
        assertEquals(validMoves.size(), 2);
        int locationSum = 0;
        for (DefaultGameMove move : validMoves) {
            assertEquals(move.getPlayerName(), "X");
            locationSum += move.getLocation();
        }
        assertEquals(locationSum, 3);
    }
    
    @Test
    public void parseUserInputMoveTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        DefaultGameMove move = gameState.getMoveFromString("2");
        assertEquals(move.getPlayerName(), "X");
        assertEquals(move.getLocation(), 2);
    }
    
    @Test
    public void validMoveTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        DefaultGameMove move = new DefaultGameMove("X", 2);
        assertTrue(gameState.isValidMove(move));
    }
    
    @Test
    public void invalidMoveWrongPlayerTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        DefaultGameMove move = new DefaultGameMove("O", 2);
        assertFalse(gameState.isValidMove(move));
    }
    
    @Test
    public void invalidMoveLocationTooHighTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        DefaultGameMove move = new DefaultGameMove("X", 4);
        assertFalse(gameState.isValidMove(move));
    }
    
    @Test
    public void invalidMoveLocationTooLowTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        DefaultGameMove move = new DefaultGameMove("X", -1);
        assertFalse(gameState.isValidMove(move));
    }
    
    @Test
    public void gameEndWinTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        gameState.applyMove(new DefaultGameMove("X", 0));
        assertFalse(gameState.isGameOver());
        gameState.applyMove(new DefaultGameMove("O", 1));
        assertFalse(gameState.isGameOver());
        gameState.applyMove(new DefaultGameMove("X", 2));
        assertTrue(gameState.isGameOver());
        GameResult gameResult = gameState.getGameResult();
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
    }
    
    @Test
    public void gameEndTieTest() {
        MNKGameState gameState = MNKGameState.of(2, 1, 2);
        gameState.applyMove(new DefaultGameMove("X", 0));
        assertFalse(gameState.isGameOver());
        gameState.applyMove(new DefaultGameMove("O", 1));
        assertTrue(gameState.isGameOver());
        GameResult gameResult = gameState.getGameResult();
        assertTrue(gameResult.isTie());
    }
    
    @Test
    public void playFullGameTest() {
        MNKGameState gameState = MNKGameState.of(2, 2, 2);
        Game<DefaultGameMove, MNKGameState> game = new Game<>(gameState);
        game.addPlayer("X", PlayerFactory.getNewAIPlayer(gameState, 10));
        game.addPlayer("O", PlayerFactory.getNewAIPlayer(gameState, 10));
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
    }
    
}
