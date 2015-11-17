package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import java.util.Deque;
import java.util.Set;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
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
public final class DefaultNodeResultsService<NR extends NodeResults> implements NodeResultsService<NR> {
    private NodeResultsFactory<NR> nodeResultsFactory;
    
    public DefaultNodeResultsService(NodeResultsFactory<NR> nodeResultsFactory) {
        this.nodeResultsFactory = nodeResultsFactory;
    }
    
    @Override
    public <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            NR getNewNodeResults(GM move, GS gameState) {
        return nodeResultsFactory.getNewNodeResults(move, gameState);
    }
    
    @Override
    public <GM extends GameMove, GR extends GameResult> 
            void propagateGameResult(GR gameResult, SearchTreeIterator<GM,NR> treeIterator) {
        Set<NR> nodeResultsSet = Sets.newIdentityHashSet();
        Deque<SearchTreeIterator<GM,NR>> iteratorDeque = Queues.newArrayDeque();
        iteratorDeque.push(treeIterator);
        while (!iteratorDeque.isEmpty()) {
            SearchTreeIterator<GM,NR> iterator = iteratorDeque.pop();
            if (nodeResultsSet.add(iterator.getCurrentNodeResults())) {
                while (iterator.hasNextParent()) {
                    iterator.advanceParentNode();
                    iteratorDeque.push(iterator.getCurrentParentIterator());
                }
            }
        }
        for (NR nodeResults : nodeResultsSet) {
            nodeResults.applyGameResult(gameResult);
        }
    }
}
