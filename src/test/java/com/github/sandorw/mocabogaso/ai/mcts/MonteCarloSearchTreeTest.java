package com.github.sandorw.mocabogaso.ai.mcts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;

public final class MonteCarloSearchTreeTest {
	
	MonteCarloSearchTree<GameMove> searchTree;
	GameMove goodMockedMove;
	GameMove badMockedMove;
	GameState<GameMove,GameResult> mockedGameState;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@SuppressWarnings("unchecked")
	@Before
	public void before() {
	    badMockedMove = mock(GameMove.class);
        goodMockedMove = mock(GameMove.class);
        List<GameMove> moveList = Lists.newArrayList();
        moveList.add(badMockedMove);
        moveList.add(goodMockedMove);
        
        mockedGameState = mock(GameState.class);
        when(mockedGameState.getAllValidMoves()).thenReturn(moveList);
        when(mockedGameState.isGameOver()).thenReturn(false);
        when(mockedGameState.getCopy()).thenReturn(mockedGameState);
        
		NodeResultsService<NodeResults> mockedNodeResultsService = mock(NodeResultsService.class);
		NodeResults badMockedResults = mock(NodeResults.class);
		NodeResults goodMockedResults = mock(NodeResults.class);
		NodeResults rootMockedResults = mock(NodeResults.class);
		when(badMockedResults.getValue()).thenReturn(0.0f);
		when(badMockedResults.getNumSimulations()).thenReturn(1);
		when(goodMockedResults.getValue()).thenReturn(1.0f);
		when(goodMockedResults.getNumSimulations()).thenReturn(2);
		when(rootMockedResults.getValue()).thenReturn(0.0f);
		when(rootMockedResults.getNumSimulations()).thenReturn(3);
		when(mockedNodeResultsService.getNewNodeResults(eq(badMockedMove), any())).thenReturn(badMockedResults);
		when(mockedNodeResultsService.getNewNodeResults(eq(goodMockedMove), any())).thenReturn(goodMockedResults);
		when(mockedNodeResultsService.getNewNodeResults(eq(null), any())).thenReturn(rootMockedResults);

		searchTree = new MonteCarloSearchTree<>(mockedNodeResultsService, mockedGameState);
	}
	
	@Test
	public void iteratorAtEndHasNextTest() {
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		assert(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void iteratorHasPreviousTest() {
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
		assert(!it.hasPrevious());
		it.next();
		assertTrue(it.hasPrevious());
	}
	
	@Test
	public void iteratorNextExceptionTest() {
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
		it.next();
		exception.expect(NoSuchElementException.class);
		it.next();
	}
	
	@Test
	public void iteratorPreviousExceptionTest() {
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
		exception.expect(NoSuchElementException.class);
		it.previous();
	}
	
	@Test
	public void iteratorReturnToRootTest() {
	    expandLeafNodeWithMockedGameState();
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
	    it.next();
	    MonteCarloSearchTree<GameMove>.SearchTreeNode rootNode = it.previous();
	    assertNull(rootNode.getAppliedMove());
	    assertFalse(it.hasPrevious());
	}
	
	@Test
	public void iteratorPreviousFromEndTest() {
	    expandLeafNodeWithMockedGameState();
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
		it.next();
		it.next();
		assertTrue(it.hasPrevious());
		MonteCarloSearchTree<GameMove>.SearchTreeNode goodMoveNode = it.previous();
		assertEquals(goodMoveNode.getAppliedMove(), goodMockedMove);
	}
	
	@Test
	public void zeroMaxDepthTest() {
		exception.expect(IllegalArgumentException.class);
		searchTree.setMaxNodeDepth(0);
	}
	
	@Test
	public void advanceTreeToMoveTest() {
		expandLeafNodeWithMockedGameState();
		searchTree.advanceTree(goodMockedMove, mockedGameState);
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		MonteCarloSearchTree<GameMove>.SearchTreeNode treeNode = it.next();
		assertEquals(treeNode.getAppliedMove(),goodMockedMove);
	}
	
	@Test
	public void doNotExpandBeyondMaxDepthTest() {
		searchTree.setMaxNodeDepth(1);
		searchTree.setNodeExpandThreshold(1);
		expandLeafNodeWithMockedGameState();
		// Should not expand due to max depth restriction
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		it.next();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void doNotExpandUnderThresholdTest() {
		searchTree.setNodeExpandThreshold(3);
		expandLeafNodeWithMockedGameState();
		// Should not expand due to node expand threshold
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		it.next();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void doNotExpandWhenGameOverTest() {
		when(mockedGameState.isGameOver()).thenReturn(true);
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void advanceToMoveNotInTreeTest() {
		searchTree.advanceTree(goodMockedMove, mockedGameState);
		SearchTreeIterator<GameMove> it = searchTree.iterator();
		MonteCarloSearchTree<GameMove>.SearchTreeNode treeNode = it.next();
		assertEquals(treeNode.getAppliedMove(),goodMockedMove);
	}
	
	@Test
	public void getMostSimulatedMoveTest() {
	    expandLeafNodeWithMockedGameState();
		assertEquals(searchTree.getMostSimulatedMove(),goodMockedMove);
	}
	
	@Test
	public void exploreBadMoveTest() {
	    expandLeafNodeWithMockedGameState();
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
	    MonteCarloSearchTree<GameMove>.SearchTreeNode rootNode = it.next();
	    MonteCarloSearchTree<GameMove>.SearchTreeNode goodMoveNode = it.next();
		when(rootNode.getNumSimulations()).thenReturn(100000001);
		when(goodMoveNode.getNumSimulations()).thenReturn(100000000);
		it = searchTree.iterator();
		it.next();
		MonteCarloSearchTree<GameMove>.SearchTreeNode badMoveNode = it.next();
		assertEquals(badMoveNode.getAppliedMove(),badMockedMove);
	}
	
	@Test
	public void rootNodeExplorationValueTest() {
	    MonteCarloSearchTree<GameMove>.SearchTreeNode rootNode = searchTree.iterator().next();
	    assertEquals(rootNode.getNodeValue(), 0.0f, 0.001f);
	}
    
	void expandLeafNodeWithMockedGameState() {
	    SearchTreeIterator<GameMove> it = searchTree.iterator();
	    MonteCarloSearchTree<GameMove>.SearchTreeNode treeNode = null;
	    while (it.hasNext())
	        treeNode = it.next();
	    treeNode.expandNode(mockedGameState);
	}
	
}
