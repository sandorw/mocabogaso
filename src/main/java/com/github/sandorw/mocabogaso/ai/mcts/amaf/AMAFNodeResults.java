package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * A NodeResults object that stores additional AMAF results - the results of simulations that were
 * run on sibling nodes but included the moved used to reach this node.
 * 
 * @author sandorw
 */
public interface AMAFNodeResults extends NodeResults {

    <GM extends GameMove, GR extends GameResult> void applyAMAFGameResult(GR gameResult);
    
}
