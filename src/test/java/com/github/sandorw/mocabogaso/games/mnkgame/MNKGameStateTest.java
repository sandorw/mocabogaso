package com.github.sandorw.mocabogaso.games.mnkgame;

import static org.junit.Assert.*;

import org.junit.Test;

public class MNKGameStateTest {
	
	@Test
	public void nextPlayerTest() {
		MNKGameState gameState = new MNKGameState(3,4,3);
		assertEquals(gameState.getNextPlayer(),"X");
		gameState.toggleCurrentPlayer();
		assertEquals(gameState.getNextPlayer(),"O");
		gameState.toggleCurrentPlayer();
		assertEquals(gameState.getNextPlayer(),"X");
	}

}
