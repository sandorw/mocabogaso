package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Set;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Node Results Service built to work with AMAF statistics.
 * 
 * @author sandorw
 */
public final class AMAFNodeResultsService<NR extends AMAFNodeResults> implements NodeResultsService<NR> {
    private NodeResultsFactory<NR> nodeResultsFactory;
    
    public AMAFNodeResultsService(NodeResultsFactory<NR> nodeResultsFactory) {
        this.nodeResultsFactory = nodeResultsFactory;
    }
    
    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> NR getNewNodeResults(GS gameState) {
        return nodeResultsFactory.getNewNodeResults(gameState);
    }

    public <GM extends GameMove> void propagateGameResultWithAMAF(GameResult gameResult, 
            SearchTreeIterator<GM,NR> treeIterator, Set<GM> playedMoves) {
        AMAFTreeWalker<GM,NR> treeWalker = new AMAFTreeWalker<>(treeIterator);
        treeWalker.applyGameResultWithPlayoutMoves(gameResult, playedMoves);
    }

    @Override
    public <GM extends GameMove> void propagateGameResult(GameResult gameResult, SearchTreeIterator<GM,NR> treeIterator) {
        throw new UnsupportedOperationException();
    }
}
