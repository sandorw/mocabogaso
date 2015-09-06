package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public interface Player<GM extends GameMove, GS extends GameState<GM,? extends GameResult>> {

    public void initialize(GS gameState);

    public GM chooseMove(GS gameState);

    public void informOfMove(GM move, GS resultingGameState);

    public void terminate();

}
