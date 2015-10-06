package com.github.sandorw.mocabogaso.ai.mcts;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Test cases for MonteCarloSearchTree.
 *
 * @author sandorw
 */
public final class MonteCarloSearchTreeTest {
    SimpleTestGameState gameState;
    MonteCarloSearchTree<DefaultGameMove,DefaultNodeResults> searchTree;

    @Before
    public void before() {
        DefaultNodeResultsService nodeResultsService = new DefaultNodeResultsService();
        gameState = new SimpleTestGameState();
        searchTree = new MonteCarloSearchTree<>(nodeResultsService, gameState);
    }
    
    @Test
    public void getMostSimulatedNodeTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        iterator.advanceChildNode();
        int moveLocation = iterator.getCurrentChildMove().getLocation();
        iterator = iterator.getCurrentChildIterator();
        DefaultNodeResults nodeResults = iterator.getCurrentNodeResults();
        DefaultGameResult gameResult = new DefaultGameResult("Player 1", false);
        nodeResults.applyGameResult(gameResult);
        DefaultGameMove mostSimulatedMove = searchTree.getMostSimulatedMove();
        assertEquals(moveLocation, mostSimulatedMove.getLocation());
    }
    
    @Test
    public void advanceToExistingMoveTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        iterator.advanceChildNode();
        DefaultGameMove move = iterator.getCurrentChildMove();
        gameState.applyMove(move);
        searchTree.advanceTree(move, gameState);
        iterator = searchTree.iterator();
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNextParent());
    }
    
    @Test
    public void advanceToMissingMoveTest() {
        DefaultGameMove move = new DefaultGameMove("Player 1", 1);
        searchTree.advanceTree(move, gameState);
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNextParent());
    }
    
    @Test
    public void dontExpandNodesWithoutEnoughSimulationsTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        DefaultGameMove move = iterator.advanceToNextExplorationNode("Player 1");
        gameState.applyMove(move);
        iterator.expandNode(gameState);
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void expandNodesWithLoweredSimulationThresholdTest() {
        searchTree.setNodeExpandThreshold(0);
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        DefaultGameMove move = iterator.advanceToNextExplorationNode("Player 1");
        gameState.applyMove(move);
        iterator.expandNode(gameState);
        assertTrue(iterator.hasNext());
    }
    
    @Test
    public void multipleParentTranspositionTableTest() {
        searchTree.setNodeExpandThreshold(0);
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        SimpleTestGameState copy = (SimpleTestGameState) gameState.getCopy();
        DefaultGameMove move = new DefaultGameMove("Player 1", 1);
        copy.applyMove(move);
        boolean expandedNode = false;
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                iterator.getCurrentChildIterator().expandNode(copy);
                expandedNode = true;
                break;
            }
        }
        assertTrue(expandedNode);
        copy = (SimpleTestGameState) gameState.getCopy();
        move = new DefaultGameMove("Player 1", 3);
        copy.applyMove(move);
        expandedNode = false;
        iterator = searchTree.iterator();
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                iterator.getCurrentChildIterator().expandNode(copy);
                expandedNode = true;
                break;
            }
        }
        assertTrue(expandedNode);
        iterator = iterator.getCurrentChildIterator();
        move = new DefaultGameMove("Player 2", 1);
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            if (iterator.getCurrentChildMove().equals(move)) {
                break;
            }
        }
        iterator = iterator.getCurrentChildIterator();
        int numParents = 0;
        while (iterator.hasNextParent()) {
            ++numParents;
            iterator.advanceParentNode();
        }
        assertEquals(numParents, 2);
    }
    
    /**
     * Iterator tests
     */
    
    @Test
    public void unexpandedTreeIteratorTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNextChild());
        assertFalse(iterator.hasNextParent());
    }
    
    @Test
    public void expandedNodeHasAllValidChildrenTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNextChild());
        int numChildren = 0;
        int moveLocationSum = 0;
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            ++numChildren;
            DefaultGameMove move = iterator.getCurrentChildMove();
            assertEquals(move.getPlayerName(), "Player 1");
            moveLocationSum += move.getLocation();
        }
        assertEquals(numChildren, 3);
        assertEquals(moveLocationSum, 6);
    }
    
    @Test
    public void getParentIteratorTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        iterator.advanceToNextExplorationNode("Player 1");
        assertTrue(iterator.hasNextParent());
        iterator.advanceParentNode();
        iterator = iterator.getCurrentParentIterator();
        assertFalse(iterator.hasNextParent());
    }
    
    @Test
    public void getChildIteratorTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        iterator.expandNode(gameState);
        iterator.advanceChildNode();
        iterator = iterator.getCurrentChildIterator();
        assertFalse(iterator.hasNextChild());
        assertTrue(iterator.hasNextParent());
    }
    
    @Test
    public void getCleanNodeResultsTest() {
        SearchTreeIterator<DefaultGameMove,DefaultNodeResults> iterator = searchTree.iterator();
        DefaultNodeResults nodeResults = iterator.getCurrentNodeResults();
        assertEquals(nodeResults.getNumSimulations(), 0);
    }	
}
