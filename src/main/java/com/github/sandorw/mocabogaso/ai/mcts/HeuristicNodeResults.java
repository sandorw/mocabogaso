package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * NodeResults that support applying virtual GameResults from heuristics.
 * 
 * @author sandorw
 */
public interface HeuristicNodeResults extends NodeResults {
    
    <GR extends GameResult> void applyVirtualGameResult(GR gameResult, int weight);
    
}
