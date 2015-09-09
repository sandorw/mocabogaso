package com.github.sandorw.mocabogaso.games.defaults;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DefaultGameMoveTest {

	DefaultGameMove move;
	
	@Before
	public void initializeMove() {
		move = new DefaultGameMove("X", 5);
	}
	
	@Test
	public void getPlayerTest() {
		assertEquals(move.getPlayer(), "X");
	}
	
	@Test
	public void getLocationTest() {
		assertEquals(move.getLocation(), 5);
	}
	
	@Test
	public void toStringTest() {
		String moveString = move.toString();
		assert(moveString.contains("DefaultGameMove"));
		assert(moveString.contains("X"));
		assert(moveString.contains("5"));
	}

}
