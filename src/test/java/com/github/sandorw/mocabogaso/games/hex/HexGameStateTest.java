package com.github.sandorw.mocabogaso.games.hex;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.players.PlayerFactory;

/**
 * Test cases for HexGameState
 * 
 * @author sandorw
 */
public class HexGameStateTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    public void disAllowTrivialGameTest() {
        exception.expect(IllegalArgumentException.class);
        HexGameState.of(1);
    }
    
    @Test
    public void copyStateTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("X", 4));
        HexGameState copy = (HexGameState) gameState.getCopy();
        gameState.applyMove(new DefaultGameMove("O", 3));
        copy.applyMove(new DefaultGameMove("O", 3));
        assertTrue(gameState.equals(copy));
        assertEquals(gameState.hashCode(), copy.hashCode());
    }
    
    @Test
    public void verifyPlayerNamesTest() {
        HexGameState gameState = HexGameState.of(3);
        List<String> playerNames = gameState.getAllPlayerNames();
        assertEquals(playerNames.size(), 2);
        assertTrue(playerNames.contains("X"));
        assertTrue(playerNames.contains("O"));
    }
    
    @Test
    public void verifyAllValidMovesTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("X", 4));
        List<DefaultGameMove> validMoves = gameState.getAllValidMoves();
        int locationSum = 0;
        for (DefaultGameMove move : validMoves) {
            locationSum += move.getLocation();
            assertEquals(move.getPlayerName(), "O");
        }
        assertEquals(validMoves.size(), 8);
        assertEquals(locationSum, 32);
    }
    
    @Test
    public void duplicateMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("X", 4));
        assertFalse(gameState.isValidMove(new DefaultGameMove("O", 4)));
    }
    
    @Test
    public void wrongPlayerMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        assertFalse(gameState.isValidMove(new DefaultGameMove("O", 4)));
    }
    
    @Test
    public void offBoardMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        assertFalse(gameState.isValidMove(new DefaultGameMove("X", -1)));
        assertFalse(gameState.isValidMove(new DefaultGameMove("X", 9)));
    }
    
    @Test
    public void validMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        assertTrue(gameState.isValidMove(new DefaultGameMove("X", 4)));
    }
    
    @Test
    public void parseInvalidPlayerInputMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        DefaultGameMove move = gameState.getMoveFromString("hi");
        assertFalse(gameState.isValidMove(move));
    }
    
    @Test
    public void parseValidPlayerInputMoveTest() {
        HexGameState gameState = HexGameState.of(3);
        DefaultGameMove move = gameState.getMoveFromString("A3");
        assertTrue(gameState.isValidMove(move));
        gameState.applyMove(move);
        assertFalse(gameState.isValidMove(new DefaultGameMove("O", 6)));
    }
    
    @Test
    public void xWinningGameTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("X", 0));
        gameState.applyMove(new DefaultGameMove("X", 2));
        gameState.applyMove(new DefaultGameMove("X", 1));
        assertTrue(gameState.isGameOver());
        GameResult gameResult = gameState.getGameResult();
        assertEquals(gameResult.getWinningPlayer(), "X");
    }
    
    @Test
    public void oWinningGameTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("O", 2));
        gameState.applyMove(new DefaultGameMove("O", 8));
        gameState.applyMove(new DefaultGameMove("O", 5));
        assertTrue(gameState.isGameOver());
        GameResult gameResult = gameState.getGameResult();
        assertEquals(gameResult.getWinningPlayer(), "O");
    }
    
    @Test
    public void simulatedGameTest() {
        HexGameState gameState = HexGameState.of(3);
        gameState.applyMove(new DefaultGameMove("X", 4));
        gameState.applyMove(new DefaultGameMove("O", 5));
        gameState.applyMove(new DefaultGameMove("X", 8));
        gameState.applyMove(new DefaultGameMove("O", 3));
        gameState.applyMove(new DefaultGameMove("X", 0));
        assertTrue(gameState.isGameOver());
        GameResult gameResult = gameState.getGameResult();
        assertEquals(gameResult.getWinningPlayer(), "X");
    }
    
    @Test
    public void unfinishedGameResultExceptionTest() {
        HexGameState gameState = HexGameState.of(3);
        exception.expect(IllegalStateException.class);
        gameState.getGameResult();
    }

    @Test
    public void playFullGameTest() {
        HexGameState gameState = HexGameState.of(3);
        Game<DefaultGameMove, HexGameState> game = new Game<>(gameState);
        game.addPlayer("X", PlayerFactory.getNewAIPlayer(gameState, 200));
        game.addPlayer("O", PlayerFactory.getNewAIPlayer(gameState, 200));
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertFalse(gameResult.isTie());
    }
    
    /**
     * Hex heuristic tests
     */
    
    @Test
    public void initialStateHeuristic_weightTest() {
        InitialStateHeuristic heuristic = new InitialStateHeuristic(5);
        assertEquals(heuristic.getWeight(), 5);
    }
    
    @Test
    public void initialStateHeuristic_isTieTest() {
        InitialStateHeuristic heuristic = new InitialStateHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        for (DefaultGameMove move : gameState.getAllValidMoves()) {
            assertTrue(heuristic.evaluateMove(move, gameState).isTie());
        }
    }
    
    @Test
    public void firstLineHeuristic_weightTest() {
        FirstLineHeuristic heuristic = new FirstLineHeuristic(5);
        assertEquals(heuristic.getWeight(), 5);
    }
    
    @Test
    public void firstLineHeuristic_badFirstLinePlayTest() {
        FirstLineHeuristic heuristic = new FirstLineHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        for (int i=0; i < 5; ++i) {
            DefaultGameMove move = new DefaultGameMove("X", i);
            DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
            assertFalse(gameResult.isTie());
            assertEquals(gameResult.getWinningPlayer(), "O");
        }
        for (int i=20; i < 25; ++i) {
            DefaultGameMove move = new DefaultGameMove("X", i);
            DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
            assertFalse(gameResult.isTie());
            assertEquals(gameResult.getWinningPlayer(), "O");
        }
        for (int i=0; i < 25; i += 5) {
            DefaultGameMove move = new DefaultGameMove("X", i);
            DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
            assertFalse(gameResult.isTie());
            assertEquals(gameResult.getWinningPlayer(), "O");
        }
        for (int i=4; i < 25; i += 5) {
            DefaultGameMove move = new DefaultGameMove("X", i);
            DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
            assertFalse(gameResult.isTie());
            assertEquals(gameResult.getWinningPlayer(), "O");
        }
    }
    
    @Test
    public void firstLineHeuristic_goodFirstLinePlayTest() {
        FirstLineHeuristic heuristic = new FirstLineHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        DefaultGameMove move = new DefaultGameMove("X", 6);
        gameState.applyMove(move);
        move = new DefaultGameMove("O", 11);
        gameState.applyMove(move);
        move = new DefaultGameMove("X", 1);
        DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
        assertNull(gameResult);
    }
    
    @Test
    public void firstLineHeuristic_interiorPlayTest() {
        FirstLineHeuristic heuristic = new FirstLineHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        DefaultGameMove move = new DefaultGameMove("X", 6);
        DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
        assertNull(gameResult);
    }
    
    @Test
    public void oneSpaceHopHeuristic_weightTest() {
        OneSpaceHopHeuristic heuristic = new OneSpaceHopHeuristic(5);
        assertEquals(heuristic.getWeight(), 5);
    }
    
    @Test
    public void oneSpaceHopHeuristic_positiveTest() {
        OneSpaceHopHeuristic heuristic = new OneSpaceHopHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        DefaultGameMove move = new DefaultGameMove("X", 12);
        gameState.applyMove(move);
        move = new DefaultGameMove("X", 1);
        DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
        move = new DefaultGameMove("X", 8);
        gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
        move = new DefaultGameMove("X", 19);
        gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
        move = new DefaultGameMove("X", 23);
        gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
        move = new DefaultGameMove("X", 16);
        gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
        move = new DefaultGameMove("X", 5);
        gameResult = heuristic.evaluateMove(move, gameState);
        assertFalse(gameResult.isTie());
        assertEquals(gameResult.getWinningPlayer(), "X");
    }
    
    @Test
    public void oneSpaceHopHeuristic_negativeTest() {
        OneSpaceHopHeuristic heuristic = new OneSpaceHopHeuristic(5);
        HexGameState gameState = HexGameState.of(5);
        DefaultGameMove move = new DefaultGameMove("X", 12);
        gameState.applyMove(move);
        move = new DefaultGameMove("O", 6);
        gameState.applyMove(move);
        move = new DefaultGameMove("X", 1);
        DefaultGameResult gameResult = heuristic.evaluateMove(move, gameState);
        assertNull(gameResult);
    }
}
