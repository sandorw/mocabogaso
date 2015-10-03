package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsService;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/**
 * Default NodeResultsService that works with DefaultNodeResults.
 *
 * @author sandorw
 */
public final class DefaultNodeResultsService implements NodeResultsService<DefaultNodeResults> {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>>
            DefaultNodeResults getNewNodeResults(GS gameState) {
        return new DefaultNodeResults(gameState);
    }
    
    @Override
    public <GM extends GameMove> void propagateGameResult(GameResult gameResult, SearchTreeIterator<GM,DefaultNodeResults> treeIterator) {
        Set<DefaultNodeResults> nodeResultsSet = Sets.newIdentityHashSet();
        Deque<SearchTreeIterator<GM,DefaultNodeResults>> iteratorDeque = Queues.newArrayDeque();
        iteratorDeque.push(treeIterator);
        while (!iteratorDeque.isEmpty()) {
            SearchTreeIterator<GM,DefaultNodeResults> iterator = iteratorDeque.pop();
            if (nodeResultsSet.add(iterator.getCurrentNodeResults())) {
                while (iterator.hasNextParent()) {
                    iterator.advanceParentNode();
                    iteratorDeque.push(iterator.getCurrentParentIterator());
                }
            }
        }
        for (DefaultNodeResults nodeResults : nodeResultsSet) {
            nodeResults.applyGameResult(gameResult);
        }
    }
}
