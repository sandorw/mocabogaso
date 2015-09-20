package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

public class DefaultNodeResultsServiceTest {

    DefaultNodeResultsService nodeResultsService;
    
    @Before
    public void before() {
        nodeResultsService = new DefaultNodeResultsService();
    }
    
    @Test
    public void getNewNodeResultsTest() {
        DefaultNodeResults nodeResults = nodeResultsService.getNewNodeResults(null, null);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue(), 0.0f, 0.001f);
    }
    
    @Test
    public void propagateGameResultTest() {
        MonteCarloSearchTree<GameMove> searchTree = new MonteCarloSearchTree<>(nodeResultsService, null);
        SearchTreeIterator<GameMove> it = searchTree.iterator();
        MonteCarloSearchTree<GameMove>.SearchTreeNode rootNode = it.next();
        GameResult mockedGameResult = mock(GameResult.class);
        nodeResultsService.propagateGameResult(mockedGameResult, it);
        assertEquals(rootNode.getNumSimulations(), 1);
    }

}
