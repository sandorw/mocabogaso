package com.github.sandorw.mocabogaso.ai.mcts;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

/**
 * Search tree of moves under consideration. Tracks the success of simulated games resulting from each
 * move via NodeResults created by the NodeResultsService that is passed in. Use the iterator method
 * to retrieve a SearchTreeIterator used to explore and operate on the tree.
 *
 * @author sandorw
 */
public final class MonteCarloSearchTree<GM extends GameMove, NR extends NodeResults> {
	private volatile SearchTreeNode rootNode;
    private volatile int NODE_EXPAND_THRESHOLD = 5;
    private volatile float EXPLORATION_CONSTANT = 1.0f;
	private final NodeResultsService<NR> nodeResultsService;
	private final Map<Long, SearchTreeNode> transpositionTable;
	
	public <GS extends GameState<GM, ? extends GameResult>> 
	        MonteCarloSearchTree(NodeResultsService<NR> nrService, GS initialGameState) {
		rootNode = null;
		nodeResultsService = nrService;
		rootNode = new SearchTreeNode(initialGameState);
		transpositionTable = new MapMaker()
		        .weakValues()
		        .makeMap();
	}
	
	public GM getMostSimulatedMove() {
	    return rootNode.getMostSimulatedChildMove();
	}
	
	public synchronized <GS extends GameState<GM, ? extends GameResult>> void advanceTree(GM move, GS resultingGameState) {
	    SearchTreeNode newRoot = rootNode.findNodeWithMove(move);
	    if (newRoot == null) {
	        long zobristHash = resultingGameState.getZobristHash();
            newRoot = new SearchTreeNode(resultingGameState);
            transpositionTable.put(zobristHash, newRoot);
	    } else {
	        newRoot.removeParentNodes();
	    }
	    rootNode = newRoot;
	}
	
	public void setNodeExpandThreshold(int threshold) {
		NODE_EXPAND_THRESHOLD = threshold;
	}
	
	public void setExplorationConstant(float explorationConstant) {
		EXPLORATION_CONSTANT = explorationConstant;
	}
	
	public SearchTreeIterator<GM,NR> iterator() {
	    return new SearchTreeIterator<>(rootNode);
	}
	
	/**
	 * Individual node within the search tree. Contains NodeResults containing information about
	 * the success rate of this state. Edges leading to other nodes include the move used to 
	 * get there.
	 *
	 * @author sandorw
	 */
	private final class SearchTreeNode {
		private final List<Pair<GM,SearchTreeNode>> parentNodes;
		private volatile boolean expanded;
		private final List<Pair<GM,SearchTreeNode>> childNodes;
		private final NR nodeResults;
		
        private <GS extends GameState<GM, ? extends GameResult>>
        		SearchTreeNode(GS resultingGameState) {
            parentNodes = Lists.newArrayList();
        	expanded = false;
        	childNodes = Lists.newArrayList();
        	nodeResults = nodeResultsService.getNewNodeResults(null, resultingGameState);
        }
        
        private void addParentNode(GM moveMade, SearchTreeNode parent) {
            parentNodes.add(Pair.of(moveMade, parent));
        }
        
        private void removeParentNodes() {
            parentNodes.clear();
        }
        
        private float getValue(String evaluatingPlayerName, int numParentSimulations) {
            return nodeResults.getValue(evaluatingPlayerName) +
                    getExplorationValue(numParentSimulations);
        }

        private float getExplorationValue(int numParentSimulations) {
            return EXPLORATION_CONSTANT*(float)Math.sqrt(Math.log(numParentSimulations+1)/(getNumSimulations()+1));
        }
        
        private int getNumSimulations() {
            return nodeResults.getNumSimulations();
        }

        private synchronized <GS extends GameState<GM, ? extends GameResult>> void expandNode(GS gameState) {
            if (!expanded && !gameState.isGameOver() &&
                    ((getNumSimulations() >= NODE_EXPAND_THRESHOLD) || (this == rootNode))) {
                for (GM move : gameState.getAllValidMoves()) {
                    @SuppressWarnings("unchecked")
                    GS resultingGameState = (GS) gameState.getCopy();
                    resultingGameState.applyMove(move);
                    long zobristHash = resultingGameState.getZobristHash();
                    SearchTreeNode childNode = null;
                    if (transpositionTable.containsKey(zobristHash)) {
                        childNode = transpositionTable.get(zobristHash);
                    }
                    if (childNode == null) {
                        childNode = new SearchTreeNode(resultingGameState);
                        transpositionTable.put(zobristHash, childNode);
                    }
                    childNode.addParentNode(move, this);
                    childNodes.add(Pair.of(move, childNode));
                }
                expanded = true;
            }
        }

        private GM getMostSimulatedChildMove() {
            if (!expanded)
                return null;
            GM mostSimulatedMove = null;
            int mostSimulations = -1;
            for (Pair<GM,SearchTreeNode> pair : childNodes) {
                int numChildSimulations = pair.getRight().getNumSimulations();
                if (numChildSimulations > mostSimulations) {
                    mostSimulations = numChildSimulations;
                    mostSimulatedMove = pair.getLeft();
                }
            }
            return mostSimulatedMove;
        }
        
        private Pair<GM,SearchTreeNode> getHighestValueChildPair(String evaluatingPlayerName) {
            if (!expanded)
                return null;
            int numParentSimulations = getNumSimulations();
            Pair<GM,SearchTreeNode> bestPair = null;
            float highestValue = Float.NEGATIVE_INFINITY;
            for (Pair<GM,SearchTreeNode> pair : childNodes) {
                float value = pair.getRight().getValue(evaluatingPlayerName, numParentSimulations);
                if (value > highestValue) {
                    highestValue = value;
                    bestPair = pair;
                }
            }
            return bestPair;
        }
        
        private SearchTreeNode findNodeWithMove(GM move) {
            if (!expanded)
                return null;
            SearchTreeNode node = null;
            for (Pair<GM,SearchTreeNode> pair : childNodes) {
                if (move.equals(pair.getLeft())) {
                    node = pair.getRight();
                    break;
                }
            }
            return node;
        }
	}
	
	/**
	 * Iterator for passing through the search tree. Provides access to both parent and child
	 * nodes. Note that this does not follow the normal iterator semantics. The iterator begins
	 * pointing at the root node.
	 * 
	 * @author sandorw
	 */
	public static final class SearchTreeIterator<GM extends GameMove, NR extends NodeResults> {
	    private MonteCarloSearchTree<GM,NR>.SearchTreeNode currentNode;
	    private int currentChildIndex;
	    private int currentParentIndex;
	    
	    private SearchTreeIterator(MonteCarloSearchTree<GM,NR>.SearchTreeNode startingNode) {
	        currentNode = startingNode;
	        resetIndices();
	    }
	    
	    private void resetIndices() {
	        currentChildIndex = -1;
	        currentParentIndex = -1;
	    }
	    
	    public boolean hasNext() {
	        return ((currentNode.expanded) && (!currentNode.childNodes.isEmpty()));
	    }
	    
	    public boolean hasNextChild() {
	        return (currentChildIndex < currentNode.childNodes.size()-1);
	    }
	    
	    public boolean hasNextParent() {
	        return (currentParentIndex < currentNode.parentNodes.size()-1);
	    }
	    
	    public GM advanceToNextExplorationNode(String evaluatingPlayerName) {
	        Pair<GM,MonteCarloSearchTree<GM,NR>.SearchTreeNode> pair = currentNode.getHighestValueChildPair(evaluatingPlayerName);
	        currentNode = pair.getRight();
	        resetIndices();
	        return pair.getLeft();
	    }
	    
	    public <GS extends GameState<GM, ? extends GameResult>> void expandNode(GS gameState) {
	        currentNode.expandNode(gameState);
	    }
	    
	    public NR getCurrentNodeResults() {
	        return currentNode.nodeResults;
	    }
	    
	    public void advanceChildNode() {
	        ++currentChildIndex;
	    }
	    
	    public void advanceParentNode() {
	        ++currentParentIndex;
	    }
	    
	    public SearchTreeIterator<GM,NR> getCurrentChildIterator() {
	        return new SearchTreeIterator<>(currentNode.childNodes.get(currentChildIndex).getRight());
	    }
	    
	    public GM getCurrentChildMove() {
	        return currentNode.childNodes.get(currentChildIndex).getLeft();
	    }
	    
	    public SearchTreeIterator<GM,NR> getCurrentParentIterator() {
	        return new SearchTreeIterator<>(currentNode.parentNodes.get(currentParentIndex).getRight());
	    }
	    
	    public GM getCurrentParentMove() {
	        return currentNode.parentNodes.get(currentParentIndex).getLeft();
	    }
	}
}
