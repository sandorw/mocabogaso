package com.github.sandorw.mocabogaso.ai;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface to back AI players. Performs searches for moves during the AI's turn, then selects
 * one.
 *
 * @author sandorw
 */
public interface AIService<GM extends GameMove> {
	
	<GR extends GameResult, GS extends GameState<GM,GR>> void searchMoves(GS currentGameState, int allottedTimeMs);
	
	GM selectMove();
	
	<GR extends GameResult, GS extends GameState<GM,GR>> void applyMove(GM move, GS resultingGameState);
	
}
