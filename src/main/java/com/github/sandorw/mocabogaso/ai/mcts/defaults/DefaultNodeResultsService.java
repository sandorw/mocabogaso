package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsService;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Default NodeResultsService that works with DefaultNodeResults.
 *
 * @author sandorw
 */
public final class DefaultNodeResultsService implements NodeResultsService<DefaultNodeResults> {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> DefaultNodeResults getNewNodeResults(
            GM appliedMove, GS resultGameState) {
        return new DefaultNodeResults();
    }

    @Override
    public <GM extends GameMove> void propagateGameResult(GameResult gameResult, SearchTreeIterator<GM> treeIterator) {
        while (treeIterator.hasPrevious()) {
            MonteCarloSearchTree<GM>.SearchTreeNode treeNode = treeIterator.previous();
            treeNode.applyGameResultFromSimulation(gameResult);
        }
    }

}
