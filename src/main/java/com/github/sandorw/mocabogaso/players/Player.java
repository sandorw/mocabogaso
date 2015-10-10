package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface for a player in a game. Generates a move whenever it is this player's turn.
 *
 * @author sandorw
 */
public interface Player<GM extends GameMove> {

	<GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(GS currentGameState);
	
	<GS extends GameState<GM, ? extends GameResult>> void informOfMoveMade(GM move, GS resultingGameState);
	
	void shutdown();
}
