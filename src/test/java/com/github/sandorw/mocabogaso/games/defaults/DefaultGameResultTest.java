package com.github.sandorw.mocabogaso.games.defaults;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DefaultGameResultTest {

	DefaultGameResult winningGameResult, tiedGameResult;
	
	@Before
	public void initializeGameResult() {
		winningGameResult = new DefaultGameResult("X", false);
		tiedGameResult = new DefaultGameResult("X", true);
	}
	
	@Test
	public void playerNameTest() {
		assertEquals(winningGameResult.getWinningPlayer(), "X");
	}

	@Test
	public void isTieTest() {
		assert(!winningGameResult.isTie());
		assert(tiedGameResult.isTie());
	}
	
	@Test
	public void toStringTest() {
		assert(winningGameResult.toString().contains("X"));
		assert(tiedGameResult.toString().contains("Tie"));
		assert(!tiedGameResult.toString().contains("X"));
	}
}
