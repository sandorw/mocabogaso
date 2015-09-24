package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * AI service for managing a Monte Carlo search tree based AI. At the edges of the search tree,
 * simulated games are played to build signal on good moves. The PlayoutPolicy determines how new
 * moves are chosen in the simulated games.
 *
 * @author sandorw
 */
public class MonteCarloSearchService<GM extends GameMove> implements AIService<GM> {

	private MonteCarloSearchTree<GM> searchTree;
	private PlayoutPolicy playoutPolicy;
	private NodeResultsService<? extends NodeResults> nodeResultsService;
	
	public <GS extends GameState<GM, ? extends GameResult>> MonteCarloSearchService(
	        NodeResultsService<? extends NodeResults> nodeResultsService, PlayoutPolicy policy, GS initialGameState) {
	    this.nodeResultsService = nodeResultsService;
	    playoutPolicy = policy;
	    searchTree = new MonteCarloSearchTree<>(nodeResultsService, initialGameState);
	}
	
	@Override
	public <GS extends GameState<GM, ? extends GameResult>>
				void searchMoves(GS currentGameState, int allottedTimeMs) {
		long timeout = System.currentTimeMillis() + (long)allottedTimeMs;
		MonteCarloSearchTree<GM>.SearchTreeNode rootNode = searchTree.iterator().next();
		rootNode.expandNode(currentGameState);
        while (System.currentTimeMillis() < timeout) {
            performPlayoutSimulation(currentGameState.getCopy());
        }
	}
	
	private <GS extends GameState<GM, ? extends GameResult>> void performPlayoutSimulation(GS playoutGameState) {
	    SearchTreeIterator<GM> treeIterator = searchTree.iterator();
	    //The gameState already reflects the move of the root node, so skip past it
	    MonteCarloSearchTree<GM>.SearchTreeNode treeNode = treeIterator.next();
	    while(treeIterator.hasNext()) {
	        treeNode = treeIterator.next();
	        playoutGameState.applyMove(treeNode.getAppliedMove());
	    }
	    GameState<GM, ? extends GameResult> expansionGameState = playoutGameState.getCopy();
	    while (!playoutGameState.isGameOver()) {
	        GM nextMove = playoutPolicy.getPlayoutMove(playoutGameState);
	        playoutGameState.applyMove(nextMove);
	    }
	    nodeResultsService.propagateGameResult(playoutGameState.getGameResult(), treeIterator);
	    treeNode.expandNode(expansionGameState);
	}

	@Override
	public GM selectMove() {
		return searchTree.getMostSimulatedMove();
	}

	@Override
	public void applyMove(GM move, GameState<GM, ? extends GameResult> resultingGameState) {
	    searchTree.advanceTree(move, resultingGameState);
	}

}
