package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Set;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.google.common.collect.Sets;

/**
 * Helper class for the AMAFNodeResultsService. Walks the tree to handle normal and AMAF updates.
 * 
 * @author sandorw
 */
public final class AMAFTreeWalker<GM extends GameMove, NR extends AMAFNodeResults> {
    private final Set<NR> nodeResultsSet;
    private final Set<NR> amafNodeResultsSet;
    private final SearchTreeIterator<GM,NR> treeIterator;

    public AMAFTreeWalker(SearchTreeIterator<GM,NR> iterator) {
        nodeResultsSet = Sets.newIdentityHashSet();
        amafNodeResultsSet = Sets.newIdentityHashSet();
        treeIterator = iterator;
    }
    
    public void applyGameResultWithPlayoutMoves(GameResult gameResult, Set<GM> playedMoves) {
        walkTreeAndCollectNodeResults(treeIterator, playedMoves);
        for (NR nodeResults : nodeResultsSet) {
            nodeResults.applyGameResult(gameResult);
        }
        for (NR nodeResults : amafNodeResultsSet) {
            nodeResults.applyAMAFGameResult(gameResult);
        }
    }
    
    private void walkTreeAndCollectNodeResults(SearchTreeIterator<GM,NR> iterator, Set<GM> playedMoves) {
        nodeResultsSet.add(iterator.getCurrentNodeResults());
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            GM childMove = iterator.getCurrentChildMove();
            if (playedMoves.contains(childMove)) {
                NR nodeResults = iterator.getCurrentChildIterator().getCurrentNodeResults();
                if (!nodeResultsSet.contains(nodeResults)) {
                    amafNodeResultsSet.add(nodeResults);
                }
            }
        }
        while (iterator.hasNextParent()) {
            iterator.advanceParentNode();
            Set<GM> updatedPlayedMoves = Sets.newHashSet(playedMoves);
            updatedPlayedMoves.add(iterator.getCurrentParentMove());
            walkTreeAndCollectNodeResults(iterator.getCurrentParentIterator(), updatedPlayedMoves);
        }
    }
}
