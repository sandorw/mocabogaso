package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory for constructing AMAFHeuristicNodeResults.
 * 
 * @author sandorw
 */
public class AMAFHeuristicNodeResultsFactory implements NodeResultsFactory<AMAFHeuristicNodeResults> {

    @Override
    public <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            AMAFHeuristicNodeResults getNewNodeResults(GM move, GS gameState) {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(gameState);
        for (Heuristic<GM,GR> heuristic : gameState.getHeuristics()) {
            GameResult heuristicGameResult = heuristic.evaluateMove(move, gameState);
            if (heuristicGameResult != null) {
                nodeResults.applyVirtualGameResult(heuristicGameResult, heuristic.getWeight());
            }
        }
        return nodeResults;
    }
}
