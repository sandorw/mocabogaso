package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory for creating NodeResults objects.
 * 
 * @author sandorw
 */
public interface NodeResultsFactory<NR extends NodeResults> {

    <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> NR getNewNodeResults(GM move, GS gameState);
    
}
