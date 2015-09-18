package com.github.sandorw.mocabogaso.ai;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

public interface AIService {
	
	<GS extends GameState<? extends GameMove, ? extends GameResult>> void searchMoves(GS currentGameState, int allottedTimeMs);
	
	GameMove selectMove();
	
	<GM extends GameMove> void applyMove(GM move, GameState<GM, ? extends GameResult> resultingGameState);
	
}
