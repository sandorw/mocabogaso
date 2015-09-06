package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public interface NodeResultsFactory<GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>, NR extends NodeResults<GM,GR>> {

    public NR getNewNodeResults(GM move, GS gameState);

}
