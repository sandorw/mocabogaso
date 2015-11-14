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

	<GR extends GameResult, GS extends GameState<GM,GR>> GM chooseNextMove(GS currentGameState);
	
	<GR extends GameResult, GS extends GameState<GM,GR>> void informOfMoveMade(GM move, GS resultingGameState);
	
	void shutdown();
}
