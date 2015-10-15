package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;
import com.google.common.collect.ImmutableList;

/**
 * Test cases for DefaultNodeResultsService.
 *
 * @author sandorw
 */
public final class DefaultNodeResultsServiceTest {
    private DefaultNodeResultsService<DefaultNodeResults> nodeResultsService;
    
    @Before
    public void before() {
        NodeResultsFactory<DefaultNodeResults> nodeResultsFactory = new DefaultNodeResultsFactory();
        nodeResultsService = new DefaultNodeResultsService<>(nodeResultsFactory);
    }
    
    @Test
    public void getNewNodeResultsTest() {
        @SuppressWarnings("rawtypes")
        GameState mockedGameState = mock(GameState.class);
        when(mockedGameState.getAllPlayerNames()).thenReturn(ImmutableList.of("Player 1", "Player 2"));
        @SuppressWarnings("unchecked")
        DefaultNodeResults nodeResults = nodeResultsService.getNewNodeResults(mockedGameState);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), 0.0f, 0.001f);
        assertEquals(nodeResults.getValue("Player 2"), 0.0f, 0.001f);
    }
    
    @Test
    public void propagateGameResultTest() {
        SimpleTestGameState gameState = new SimpleTestGameState();
        MonteCarloSearchTree<DefaultGameMove,DefaultNodeResults> searchTree = new MonteCarloSearchTree<>(nodeResultsService, gameState);
        searchTree.setNodeExpandThreshold(0);
        searchTree.iterator().expandNode(gameState);
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        while (iterator.hasNext()) {
            String evaluatingPlayerName = gameState.getNextPlayerName();
            DefaultGameMove move = iterator.advanceToNextExplorationNode(evaluatingPlayerName);
            gameState.applyMove(move);
        }
        DefaultGameResult gameResult = new DefaultGameResult("Player 1", false);
        nodeResultsService.propagateGameResult(gameResult, iterator);
        assertEquals(searchTree.iterator().getCurrentNodeResults().getNumSimulations(), 1);
    }

}
