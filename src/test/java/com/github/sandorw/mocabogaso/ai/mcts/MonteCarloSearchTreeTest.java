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
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeNode;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;

public final class MonteCarloSearchTreeTest {
	
	MonteCarloSearchTree searchTree;
	GameMove goodMockedMove;
	GameMove badMockedMove;
	GameState<GameMove,GameResult> mockedGameState;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@SuppressWarnings("unchecked")
	@Before
	public void before() {
		NodeResultsService<NodeResults> mockedNodeResultsService = (NodeResultsService<NodeResults>) mock(NodeResultsService.class);
		searchTree = new MonteCarloSearchTree(mockedNodeResultsService);
		badMockedMove = mock(GameMove.class);
		goodMockedMove = mock(GameMove.class);
		List<GameMove> moveList = Lists.newArrayList();
		moveList.add(badMockedMove);
		moveList.add(goodMockedMove);
		mockedGameState = (GameState<GameMove,GameResult>) mock(GameState.class);
		when(mockedGameState.getAllValidMoves()).thenReturn(moveList);
		when(mockedGameState.isGameOver()).thenReturn(false);
		when(mockedGameState.getCopy()).thenReturn(mockedGameState);
		NodeResults badMockedResults = mock(NodeResults.class);
		NodeResults goodMockedResults = mock(NodeResults.class);
		NodeResults rootMockedResults = mock(NodeResults.class);
		when(badMockedResults.getValue()).thenReturn(0.0f);
		when(badMockedResults.getNumSimulations()).thenReturn(1);
		when(goodMockedResults.getValue()).thenReturn(1.0f);
		when(goodMockedResults.getNumSimulations()).thenReturn(2);
		when(rootMockedResults.getNumSimulations()).thenReturn(3);
		when(mockedNodeResultsService.getNewNodeResults(eq(badMockedMove), any())).thenReturn(badMockedResults);
		when(mockedNodeResultsService.getNewNodeResults(eq(goodMockedMove), any())).thenReturn(goodMockedResults);
		when(mockedNodeResultsService.getNewNodeResults(eq(null), any())).thenReturn(rootMockedResults);
		searchTree.initialize(mockedGameState);
	}
	
	@Test
	public void iteratorHasNextTest() {
		SearchTreeIterator it = searchTree.iterator();
		assert(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void iteratorPreviousTest() {
		SearchTreeIterator it = searchTree.iterator();
		assert(!it.hasPrevious());
		it.next();
		assertTrue(it.hasPrevious());
	}
	
	@Test
	public void iteratorNextExceptionTest() {
		SearchTreeIterator it = searchTree.iterator();
		it.next();
		exception.expect(NoSuchElementException.class);
		it.next();
	}
	
	@Test
	public void iteratorPreviousExceptionTest() {
		SearchTreeIterator it = searchTree.iterator();
		exception.expect(NoSuchElementException.class);
		it.previous();
	}
	
	@Test
	public void iteratorNextAndPreviousTest() {
		SearchTreeIterator it = searchTree.iterator();
		it.next();
		it.previous();
		it.next();
		it.previous();
		assertTrue(it.hasNext());
		assertFalse(it.hasPrevious());
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
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();
		assertEquals(treeNode.getAppliedMove(),goodMockedMove);
	}
	
	@Test
	public void doNotExpandBeyondMaxDepthTest() {
		searchTree.setMaxNodeDepth(1);
		searchTree.setNodeExpandThreshold(1);
		expandLeafNodeWithMockedGameState();
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator it = searchTree.iterator();
		it.next();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void doNotExpandUnderThresholdTest() {
		searchTree.setNodeExpandThreshold(3);
		expandLeafNodeWithMockedGameState();
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator it = searchTree.iterator();
		it.next();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void doNotExpandWhenGameOverTest() {
		when(mockedGameState.isGameOver()).thenReturn(true);
		expandLeafNodeWithMockedGameState();
		SearchTreeIterator it = searchTree.iterator();
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void advanceToMoveNotInTreeTest() {
		searchTree.advanceTree(goodMockedMove, mockedGameState);
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();
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
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode rootNode = it.next();
		SearchTreeNode goodMoveNode = it.next();
		when(rootNode.getNodeResults().getNumSimulations()).thenReturn(100000001);
		when(goodMoveNode.getNodeResults().getNumSimulations()).thenReturn(100000000);
		it = searchTree.iterator();
		it.next();
		SearchTreeNode badMoveNode = it.next();
		assertEquals(badMoveNode.getAppliedMove(),badMockedMove);
	}
    
	void expandLeafNodeWithMockedGameState() {
	    SearchTreeIterator it = searchTree.iterator();
	    SearchTreeNode treeNode = null;
	    while (it.hasNext())
	        treeNode = it.next();
	    treeNode.expandNode(mockedGameState);
	}
	
}
