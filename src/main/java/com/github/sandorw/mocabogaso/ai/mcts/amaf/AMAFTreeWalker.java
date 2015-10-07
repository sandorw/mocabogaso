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
public class AMAFTreeWalker<GM extends GameMove> {
    private final Set<AMAFNodeResults> nodeResultsSet;
    private final Set<AMAFNodeResults> amafNodeResultsSet;
    private final SearchTreeIterator<GM,AMAFNodeResults> treeIterator;

    public AMAFTreeWalker(SearchTreeIterator<GM,AMAFNodeResults> iterator) {
        nodeResultsSet = Sets.newIdentityHashSet();
        amafNodeResultsSet = Sets.newIdentityHashSet();
        treeIterator = iterator;
    }
    
    public void applyGameResultWithPlayoutMoves(GameResult gameResult, Set<GM> playedMoves) {
        walkTreeAndCollectNodeResults(treeIterator, playedMoves);
        for (AMAFNodeResults nodeResults : nodeResultsSet) {
            nodeResults.applyGameResult(gameResult);
        }
        for (AMAFNodeResults nodeResults : amafNodeResultsSet) {
            nodeResults.applyAMAFGameResult(gameResult);
        }
    }
    
    private void walkTreeAndCollectNodeResults(SearchTreeIterator<GM,AMAFNodeResults> iterator, Set<GM> playedMoves) {
        nodeResultsSet.add(iterator.getCurrentNodeResults());
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            GM childMove = iterator.getCurrentChildMove();
            if (playedMoves.contains(childMove)) {
                AMAFNodeResults nodeResults = iterator.getCurrentChildIterator().getCurrentNodeResults();
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
