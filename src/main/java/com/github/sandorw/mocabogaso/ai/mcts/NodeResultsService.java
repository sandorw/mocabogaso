package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

public interface NodeResultsService<NR extends NodeResults> {
	
	//TODO
	//How does this service understand how to create NodeResults? Can I specify a constructor/factory interface?
	//The NodeResults interface could include a method that returns a generic NodeResults (should be static though)
	
	<GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> NR getNewNodeResults(GM appliedMove, GS resultGameState);
	
	void propagateGameResults(GameResult gameResult, SearchTreeIterator treeIterator);
	
}
