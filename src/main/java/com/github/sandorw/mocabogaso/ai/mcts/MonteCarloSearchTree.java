package com.github.sandorw.mocabogaso.ai.mcts;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;

/**
 * Search tree of moves under consideration. Tracks the success of simulated games resulting from each
 * move via NodeResults created by the NodeResultsService that is passed in. Use the iterator method
 * to retrieve a SearchTreeIterator used to access SearchTreeNodes.
 *
 * @author sandorw
 */
public final class MonteCarloSearchTree {
	private volatile SearchTreeNode rootNode;
	private volatile int MAX_NODE_DEPTH = 10;
    private volatile int NODE_EXPAND_THRESHOLD = 10;
    private volatile float EXPLORATION_CONSTANT = 1.0f;
	private final NodeResultsService<? extends NodeResults> nodeResultsService;
	
	public MonteCarloSearchTree(NodeResultsService<? extends NodeResults> nrService,
	        GameState<? extends GameMove, ? extends GameResult> initialGameState) {
		rootNode = null;
		nodeResultsService = nrService;
		rootNode = new SearchTreeNode(null, 0, null, initialGameState);
	}
	
	public GameMove getMostSimulatedMove() {
		if ((rootNode == null) || !rootNode.expanded || rootNode.childNodes.isEmpty())
			return null;
		GameMove mostSimulatedMove = null;
		int mostSimulations = -1;
		for (SearchTreeNode childNode : rootNode.childNodes) {
			int childSimulations = childNode.getNumSimulations();
			if (childSimulations > mostSimulations) {
				mostSimulations = childSimulations;
				mostSimulatedMove = childNode.appliedMove;
			}
		}
		return mostSimulatedMove;
	}
	
	public synchronized <GM extends GameMove> void advanceTree(GM move, GameState<GM, ? extends GameResult> resultingGameState) {
		SearchTreeNode newRoot = null;
		if (rootNode != null)
			for (SearchTreeNode childNode : rootNode.childNodes)
				if (childNode.getAppliedMove().equals(move)) {
					newRoot = childNode;
					newRoot.removeParentNode();
					break;
				}
		if (newRoot == null)
			newRoot = new SearchTreeNode(move, 0, null, resultingGameState);
		rootNode = newRoot;		
	}
	
	public synchronized SearchTreeIterator iterator() {
		return new SearchTreeIterator(rootNode);
    }
	
	public void setMaxNodeDepth(int maxDepth) {
		if (maxDepth < 1)
			throw new IllegalArgumentException();
		MAX_NODE_DEPTH = maxDepth;
	}
	
	public void setNodeExpandThreshold(int threshold) {
		NODE_EXPAND_THRESHOLD = threshold;
	}
	
	public void setExplorationConstant(float explorationConstant) {
		EXPLORATION_CONSTANT = explorationConstant;
	}
	
	/**
	 * Individual node within the search tree. Contains the move to be applied at the node as well
	 * as the NodeResults containing information about the success rate of the move. 
	 *
	 * @author sandorw
	 */
	public final class SearchTreeNode {
		private final GameMove appliedMove;
		private volatile SearchTreeNode parentNode;
		private volatile boolean expanded;
		private volatile List<SearchTreeNode> childNodes;
		private final NodeResults nodeResults;
        private final int nodeDepth;
         
        public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> 
        		SearchTreeNode(GM appliedMove, int depth, SearchTreeNode parent, GS resultingGameState) {
        	this.appliedMove = appliedMove;
        	parentNode = parent;
        	expanded = false;
        	childNodes = Lists.newArrayList();
        	nodeResults = nodeResultsService.getNewNodeResults(appliedMove, resultingGameState);
        	nodeDepth = depth;
        }
        
        public GameMove getAppliedMove() {
        	return appliedMove;
        }
        
        public NodeResults getNodeResults() {
        	return nodeResults;
        }
        
        public float getNodeValue() {
        	return nodeResults.getValue() + getNodeExplorationValue();
        }
        
        private float getNodeExplorationValue() {
        	if (parentNode == null)
                return 0.0f;
            int parentSimulations = parentNode.getNumSimulations();
            return EXPLORATION_CONSTANT*(float)Math.sqrt(Math.log(parentSimulations+1)/(getNumSimulations()+1));
        }
        
        public int getNumSimulations() {
            return nodeResults.getNumSimulations();
        }
        
        public synchronized <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> void expandNode(GS gameState) {
        	if (!expanded && (getNodeDepth() < MAX_NODE_DEPTH) && !gameState.isGameOver() && 
        			((getNumSimulations() >= NODE_EXPAND_THRESHOLD) || (getNodeDepth() == 0))) {
        		for (GM move : gameState.getAllValidMoves()) {
        			@SuppressWarnings("unchecked")
					GS resultingGameState = (GS) gameState.getCopy();
        			resultingGameState.applyMove(move);
        			childNodes.add(new SearchTreeNode(move, nodeDepth+1, this, resultingGameState));
        		}
        		expanded = true;
        	}
        }
        
        private int getNodeDepth() {
            return nodeDepth - rootNode.nodeDepth;
        }
        
        public void removeParentNode() {
            parentNode = null;
        }
	}
	
	/**
	 * Iterator for moving through the search tree. Can move forward (deeper) and backwards (toward
	 * the root node). When moving forward, the iterator moves to the child node with the highest
	 * node value (the move that should be explored in the next simulated game).
	 *
	 * @author sandorw
	 */
	public static final class SearchTreeIterator implements Iterator<SearchTreeNode> {
		private boolean atEnd;
		private SearchTreeNode nextNode;
		
		//TODO: consider refactoring to allow iteration through arbitrary child nodes. AMAF will need this.
		
		public SearchTreeIterator(SearchTreeNode rootNode) {
			atEnd = false;
			nextNode = rootNode;
		}
		
		@Override
		public boolean hasNext() {
			return !atEnd;
		}
		
		public boolean hasPrevious() {
			if (atEnd)
				return nextNode != null;
			return nextNode.parentNode != null;
		}
		
		@Override
		public SearchTreeNode next() {
			if (atEnd)
				throw new NoSuchElementException();
			SearchTreeNode returnNode = nextNode;
			advanceToNextExplorationNode();
			return returnNode;
		}
		
		private void advanceToNextExplorationNode() {
			if (!nextNode.expanded || nextNode.childNodes.isEmpty()) {
				atEnd = true;
				return;
			}
			SearchTreeNode explorationNode = null;
			float bestNodeValue = Float.NEGATIVE_INFINITY;
			for (SearchTreeNode childNode : nextNode.childNodes) {
				float childNodeValue = childNode.getNodeValue();
				if (childNodeValue > bestNodeValue) {
					explorationNode = childNode;
					bestNodeValue = childNodeValue;
				}
			}
			nextNode = explorationNode;
		}
		
		public SearchTreeNode previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			if (atEnd) {
				atEnd = false;
				return nextNode;
			}
			nextNode = nextNode.parentNode;
			return nextNode;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
