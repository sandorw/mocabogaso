package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Set;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Node Results Service built to work with AMAF statistics.
 * 
 * @author sandorw
 */
public class AMAFNodeResultsService implements NodeResultsService<AMAFNodeResults> {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>>
            AMAFNodeResults getNewNodeResults(GS gameState) {
        return new AMAFNodeResults(gameState);
    }

    public <GM extends GameMove> void propagateGameResultWithAMAF(GameResult gameResult, 
            SearchTreeIterator<GM,AMAFNodeResults> treeIterator, Set<GM> playedMoves) {
        AMAFTreeWalker<GM> treeWalker = new AMAFTreeWalker<>(treeIterator);
        treeWalker.applyGameResultWithPlayoutMoves(gameResult, playedMoves);
    }

    @Override
    public <GM extends GameMove> void propagateGameResult(GameResult gameResult, SearchTreeIterator<GM,AMAFNodeResults> treeIterator) {
        throw new UnsupportedOperationException();
    }
}
