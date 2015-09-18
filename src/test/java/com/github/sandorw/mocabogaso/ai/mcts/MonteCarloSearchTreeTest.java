package com.github.sandorw.mocabogaso.ai.mcts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Ignore;
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
		assert(!it.hasNext());
	}
	
	@Test
	public void iteratorPreviousTest() {
		SearchTreeIterator it = searchTree.iterator();
		assert(!it.hasPrevious());
		it.next();
		assert(it.hasPrevious());
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
		assert(it.hasNext());
		assert(!it.hasPrevious());
	}
	
	@Test
	public void zeroMaxDepthTest() {
		exception.expect(IllegalArgumentException.class);
		searchTree.setMaxNodeDepth(0);
	}
	
	@Test
	public void advanceTreeToMoveTest() {
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();		
		treeNode.expandNode(mockedGameState);
		searchTree.advanceTree(goodMockedMove, mockedGameState);
		it = searchTree.iterator();
		treeNode = it.next();
		assertEquals(treeNode.getAppliedMove(),goodMockedMove);
	}
	
	@Test
	public void doNotExpandBeyondMaxDepthTest() {
		searchTree.setMaxNodeDepth(1);
		searchTree.setNodeExpandThreshold(1);
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();
		treeNode.expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		it.next().expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		it.next();
		assert(!it.hasNext());
	}
	
	@Ignore
	@Test
	public void doNotExpandUnderThresholdTest() {
		searchTree.setNodeExpandThreshold(2);
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();
		treeNode.expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		it.next().expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		it.next();
		assert(!it.hasNext());
	}
	
	@Test
	public void doNotExpandWhenGameOverTest() {
		when(mockedGameState.isGameOver()).thenReturn(true);
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();
		treeNode.expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		assert(!it.hasNext());
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
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode treeNode = it.next();		
		treeNode.expandNode(mockedGameState);
		assertEquals(searchTree.getMostSimulatedMove(),goodMockedMove);
	}
	
	@Test
	public void exploreBadMoveTest() {
		SearchTreeIterator it = searchTree.iterator();
		SearchTreeNode rootNode = it.next();
		rootNode.expandNode(mockedGameState);
		it = searchTree.iterator();
		it.next();
		SearchTreeNode treeNode = it.next();
		when(rootNode.getNodeResults().getNumSimulations()).thenReturn(100000001);
		when(treeNode.getNodeResults().getNumSimulations()).thenReturn(100000000);
		it = searchTree.iterator();
		it.next();
		treeNode = it.next();
		assertEquals(treeNode.getAppliedMove(),badMockedMove);
	}

}
