package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface representing a service for interacting with NodeResults. Creates new instances for the
 * search tree and walks the tree to apply the results of simulated games.
 *
 * @author sandorw
 */
public interface NodeResultsService<NR extends NodeResults> {
		
	<GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> NR getNewNodeResults(GM move, GS initialGameState);
	
	<GM extends GameMove, GR extends GameResult> void propagateGameResult(GR gameResult, SearchTreeIterator<GM,NR> treeIterator);
	
}
