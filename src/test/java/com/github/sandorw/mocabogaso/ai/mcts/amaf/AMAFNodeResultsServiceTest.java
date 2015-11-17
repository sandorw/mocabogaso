package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;
import com.google.common.collect.Sets;

/**
 * Test cases for AMAFNodeResultsService
 * 
 * @author sandorw
 */
public class AMAFNodeResultsServiceTest {
    
    @Test
    public void propagateAMAFGameResultTest() {
        NodeResultsFactory<DefaultAMAFNodeResults> nodeResultsFactory = new DefaultAMAFNodeResultsFactory();
        AMAFNodeResultsService<DefaultAMAFNodeResults> nodeResultsService = new AMAFNodeResultsService<>(nodeResultsFactory);
        SimpleTestGameState gameState = new SimpleTestGameState();
        MonteCarloSearchTree<DefaultGameMove,DefaultAMAFNodeResults> searchTree = new MonteCarloSearchTree<>(nodeResultsService, gameState);
        propagateAMAFGameResult(nodeResultsService, gameState, searchTree);
    }
    
    private <NR extends AMAFNodeResults> void propagateAMAFGameResult(AMAFNodeResultsService<NR> nodeResultsService, 
            SimpleTestGameState gameState, MonteCarloSearchTree<DefaultGameMove,NR> searchTree) {
        SearchTreeIterator<DefaultGameMove,NR> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        DefaultGameMove move = new DefaultGameMove("Player 1", 1);
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                break;
            }
        }
        iterator = iterator.getCurrentChildIterator();
        move = new DefaultGameMove("Player 1", 3);
        Set<DefaultGameMove> playedMoves = Sets.newHashSet(move);
        DefaultGameResult gameResult = new DefaultGameResult("Player 1", false);
        nodeResultsService.propagateGameResultWithAMAF(gameResult, iterator, playedMoves);
        iterator = searchTree.iterator();
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                break;
            }
        }
        iterator = iterator.getCurrentChildIterator();
        NR nodeResults = iterator.getCurrentNodeResults();
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
        assertEquals(nodeResults.getNumSimulations(), 0);
        move = new DefaultGameMove("Player 1", 2);
        iterator = searchTree.iterator();
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                break;
            }
        }
        iterator = iterator.getCurrentChildIterator();
        nodeResults = iterator.getCurrentNodeResults();
        assertEquals(nodeResults.getValue("Player 1"), 0.0f, 0.001f);
        assertEquals(nodeResults.getNumSimulations(), 0);
    }

}
