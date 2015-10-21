package com.github.sandorw.mocabogaso.games.connectx;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.players.PlayerFactory;

public class ConnectXGameStateTest {

    @Test
    public void invalidMoveAboveEmptyRowTest() {
        ConnectXGameState gameState = ConnectXGameState.of(3,3,3);
        assertFalse(gameState.isValidMove(new DefaultGameMove("X", 3)));
    }
    
    @Test
    public void validMoveAtBottomEmptyRowTest() {
        ConnectXGameState gameState = ConnectXGameState.of(3,3,3);
        gameState.applyMove(new DefaultGameMove("X",0));
        assertTrue(gameState.isValidMove(new DefaultGameMove("O", 3)));
    }
    
    @Test
    public void copyEqualityTest() {
        ConnectXGameState gameState = ConnectXGameState.of(3,4,3);
        gameState.applyMove(new DefaultGameMove("X",3));
        gameState.applyMove(new DefaultGameMove("O",1));
        gameState.applyMove(new DefaultGameMove("X",2));
        ConnectXGameState copy = (ConnectXGameState) gameState.getCopy();
        assertTrue(gameState.equals(copy));
    }
    
    @Test
    public void getAllValidMovesTest() {
        ConnectXGameState gameState = ConnectXGameState.of(3,3,3);
        gameState.applyMove(new DefaultGameMove("X",0));
        List<DefaultGameMove> moves = gameState.getAllValidMoves();
        int locationSum = 0;
        for (DefaultGameMove move : moves) {
            locationSum += move.getLocation();
            assertEquals(move.getPlayerName(), "O");
        }
        assertEquals(locationSum, 6);
    }
    
    @Test
    public void getValidMoveFromString() {
        ConnectXGameState gameState = ConnectXGameState.of(3,3,3);
        gameState.applyMove(new DefaultGameMove("X",0));
        DefaultGameMove move = gameState.getMoveFromString("0");
        assertEquals(move.getPlayerName(), "O");
        assertEquals(move.getLocation(), 3);
    }
    
    @Test
    public void playFullGameTest() {
        ConnectXGameState gameState = ConnectXGameState.of(3,3,3);
        Game<DefaultGameMove, MNKGameState> game = new Game<>(gameState);
        game.addPlayer("X", PlayerFactory.getNewAIPlayer(gameState, 50));
        game.addPlayer("O", PlayerFactory.getNewAIPlayer(gameState, 50));
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isTie());
    }

}
