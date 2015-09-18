package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

public interface Player {

	<GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(GS currentGameState);
	
	<GM extends GameMove> void informOfMoveMade(GM move);
	
}
