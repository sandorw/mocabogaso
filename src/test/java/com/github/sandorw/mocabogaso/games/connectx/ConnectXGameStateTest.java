package com.github.sandorw.mocabogaso.games.connectx;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;

public class ConnectXGameStateTest {

	ConnectXGameState gameState;
	
	@Before
	public void initializeGameState() {
		gameState = new ConnectXGameState(5,7,3);
	}
	
	public void applySomeMoves() {
		DefaultGameMove move = new DefaultGameMove("X",4);
		gameState.applyMove(move);
		move = new DefaultGameMove("O",11);
		gameState.applyMove(move);
	}
	
	@Test
	public void validMoveTest() {
		applySomeMoves();
		DefaultGameMove move = new DefaultGameMove("X",18);
		assert(gameState.isValidMove(move));
	}
	
	@Test
	public void invalidMoveTest() {
		applySomeMoves();
		DefaultGameMove move = new DefaultGameMove("O",18);
		assertFalse(gameState.isValidMove(move));
		move = new DefaultGameMove("X",11);
		assertFalse(gameState.isValidMove(move));
	}
	
	@Test
	public void getCopyTest() {
		applySomeMoves();
		ConnectXGameState gameStateCopy = gameState.getCopy();
		assertEquals(gameState.toString(), gameStateCopy.toString());
	}
	
	@Test
	public void getAllValidMovesTest() {
		applySomeMoves();
		assertEquals(gameState.getAllValidMoves().size(), 7);
		for (DefaultGameMove move : gameState.getAllValidMoves()) {
			assertEquals(move.getPlayer(), "X");
			assert(gameState.isValidMove(move));
		}
	}
	
	@Test
	public void parsePlayerInputMoveTest() {
		applySomeMoves();
		DefaultGameMove move = gameState.getMoveFromString("4");
		assertEquals(move.getPlayer(), "X");
		assertEquals(move.getLocation(), 18);
	}

}
