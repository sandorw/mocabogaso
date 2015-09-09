package com.github.sandorw.mocabogaso.games.mnkgame;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;

public class MNKGameStateTest {
	
	MNKGameState gameState;
	
	@Before
	public void beforeTest() {
		gameState = new MNKGameState(5,7,3);
	}
	
	public void applySomeMoves() {
		DefaultGameMove move = new DefaultGameMove("X",4);
		gameState.applyMove(move);
		move = new DefaultGameMove("O",7);
		gameState.applyMove(move);
	}
	
	@Test
	public void nextPlayerTest() {
		assertEquals(gameState.getNextPlayer(),"X");
		gameState.toggleCurrentPlayer();
		assertEquals(gameState.getNextPlayer(),"O");
		gameState.toggleCurrentPlayer();
		assertEquals(gameState.getNextPlayer(),"X");
	}

	@Test
	public void getPlayerNamesTest() {
		List<String> playerNames = gameState.getAllPlayerNames();
		assert(playerNames.contains("X"));
		assert(playerNames.contains("O"));
	}
	
	@Test
	public void wrongPlayerMoveTest() {
		DefaultGameMove move = new DefaultGameMove("O",0);
		assertEquals(gameState.isValidMove(move),false);
	}
	
	@Test
	public void offBoardMoveTest() {
		DefaultGameMove move = new DefaultGameMove("X",-1);
		assertEquals(gameState.isValidMove(move),false);
	}
	
	@Test
	public void validMoveTest() {
		DefaultGameMove move = new DefaultGameMove("X",0);
		assertEquals(gameState.isValidMove(move),true);
	}
	
	@Test
	public void applyAndCheckValidMovesTest() {
		applySomeMoves();
		int locationSum = 0;
		List<DefaultGameMove> validMoves = gameState.getAllValidMoves();
		for (DefaultGameMove validMove : validMoves) {
			assertEquals(validMove.getPlayer(),"X");
			locationSum += validMove.getLocation();
			assertNotEquals(validMove.getLocation(),4);
			assertNotEquals(validMove.getLocation(),7);
		}
		//Sum of 1-34 minus 4 minus 7
		assertEquals(locationSum,584);
	}
	
	@Test
	public void getRowNumberTest() {
		assertEquals(gameState.getRowNumber(17),2);
	}
	
	@Test
	public void getColNumberTest() {
		assertEquals(gameState.getColNumber(17),3);
	}

	@Test
	public void boardStatusTest() {
		applySomeMoves();
		assertEquals(gameState.boardLocationStatus[0][0].toString(), " ");
		assertEquals(gameState.boardLocationStatus[gameState.getRowNumber(4)][gameState.getColNumber(4)].toString(), "X");
		assertEquals(gameState.boardLocationStatus[gameState.getRowNumber(7)][gameState.getColNumber(7)].toString(), "O");
	}
	
	@Test
	public void getCopyTest() {
		applySomeMoves();
		MNKGameState gameStateCopy = gameState.getCopy();
		assertEquals(gameState.currentPlayer, gameStateCopy.currentPlayer);
		assertEquals(gameState.numRows, gameStateCopy.numRows);
		assertEquals(gameState.numCols, gameStateCopy.numCols);
		assertEquals(gameState.numInARow, gameStateCopy.numInARow);
		assertEquals(gameState.winner, gameStateCopy.winner);
		for (int i=0; i < gameState.numRows; ++i)
			for (int j=0; j < gameState.numCols; ++j)
				assertEquals(gameState.boardLocationStatus[i][j], gameStateCopy.boardLocationStatus[i][j]);
	}
	
	@Test
	public void diagWinTest() {
		gameState.boardLocationStatus[0][0] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[1][1] = MNKGameState.BoardStatus.X;
		DefaultGameMove move = new DefaultGameMove("X",16);
		gameState.applyMove(move);
		assert(gameState.isGameOver());
		assertEquals(gameState.getGameResult().getWinningPlayer(), "X");
	}
	
	@Test
	public void horizontalWinTest() {
		gameState.boardLocationStatus[0][0] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[0][1] = MNKGameState.BoardStatus.X;
		DefaultGameMove move = new DefaultGameMove("X",2);
		gameState.applyMove(move);
		assert(gameState.isGameOver());
		assertEquals(gameState.getGameResult().getWinningPlayer(), "X");
	}
	
	@Test
	public void verticalWinTest() {
		gameState.boardLocationStatus[0][0] = MNKGameState.BoardStatus.O;
		gameState.boardLocationStatus[2][0] = MNKGameState.BoardStatus.O;
		DefaultGameMove move = new DefaultGameMove("O",7);
		gameState.applyMove(move);
		assert(gameState.isGameOver());
		assertEquals(gameState.getGameResult().getWinningPlayer(), "O");
	}
	
	@Test
	public void noWinnerTest() {
		assert(!gameState.isGameOver());
		assertEquals(gameState.getGameResult().getWinningPlayer(), "no one");
	}
	
	@Test
	public void tieTest() {
		gameState = new MNKGameState(3,3,4);
		gameState.boardLocationStatus[0][0] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[0][1] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[0][2] = MNKGameState.BoardStatus.O;
		gameState.boardLocationStatus[1][0] = MNKGameState.BoardStatus.O;
		gameState.boardLocationStatus[1][1] = MNKGameState.BoardStatus.O;
		gameState.boardLocationStatus[1][2] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[2][0] = MNKGameState.BoardStatus.X;
		gameState.boardLocationStatus[2][1] = MNKGameState.BoardStatus.O;
		DefaultGameMove move = new DefaultGameMove("X",8);
		gameState.applyMove(move);
		assert(gameState.isGameOver());
		assert(gameState.getGameResult().isTie());
	}
	
	@Test
	public void toStringTest() {
		String gameString = gameState.toString();
		assert(gameString.contains("6"));
		assert(gameString.contains("Next player: X"));
	}
	
	@Test
	public void parsePlayerInputTest() {
		DefaultGameMove move = gameState.getMoveFromString("4");
		assertEquals(move.getPlayer(), "X");
		assertEquals(move.getLocation(), 4);
	}
	
}
