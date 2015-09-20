package com.github.sandorw.mocabogaso.ai;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

public interface AIService<GM extends GameMove> {
	
	<GS extends GameState<GM, ? extends GameResult>> void searchMoves(GS currentGameState, int allottedTimeMs);
	
	GM selectMove();
	
	void applyMove(GM move, GameState<GM, ? extends GameResult> resultingGameState);
	
}
