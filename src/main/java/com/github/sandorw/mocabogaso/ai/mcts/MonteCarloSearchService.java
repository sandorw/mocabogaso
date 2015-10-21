package com.github.sandorw.mocabogaso.ai.mcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public final class MonteCarloSearchService<GM extends GameMove, NR extends NodeResults> implements AIService<GM> {
    private static Logger LOGGER = LoggerFactory.getLogger(MonteCarloSearchService.class);

	private final MonteCarloSearchTree<GM,NR> searchTree;
	private final PlayoutPolicy playoutPolicy;
	private final NodeResultsService<NR> nodeResultsService;

	public <GS extends GameState<GM, ? extends GameResult>>
	        MonteCarloSearchService(NodeResultsService<NR> nodeResultsService, PlayoutPolicy policy, GS initialGameState) {
	    this.nodeResultsService = nodeResultsService;
	    playoutPolicy = policy;
	    searchTree = new MonteCarloSearchTree<>(nodeResultsService, initialGameState);
	}

    public void setNodeExpandThreshold(int threshold) {
        searchTree.setNodeExpandThreshold(threshold);
    }

    public void setExplorationConstant(float explorationConstant) {
        searchTree.setExplorationConstant(explorationConstant);
    }

	@Override
	public <GS extends GameState<GM, ? extends GameResult>>
	        void searchMoves(GS currentGameState, int allottedTimeMs) {
	    int numSimulations = 0;
	    long timeout = System.currentTimeMillis() + (long)allottedTimeMs;
	    searchTree.iterator().expandNode(currentGameState);
	    while (System.currentTimeMillis() < timeout) {
	        performPlayoutSimulation(currentGameState.getCopy());
	        ++numSimulations;
	    }
	    LOGGER.info("Performed {} simulations in {} ms", numSimulations, allottedTimeMs);
	    logMoveChoices(currentGameState);
	}

	private <GS extends GameState<GM, ? extends GameResult>> void performPlayoutSimulation(GS playoutGameState) {
	    SearchTreeIterator<GM,NR> iterator = searchTree.iterator();
	    while (iterator.hasNext()) {
	        String currentPlayerName = playoutGameState.getNextPlayerName();
	        GM move = iterator.advanceToNextExplorationNode(currentPlayerName);
	        playoutGameState.applyMove(move);
	    }
	    GameState<GM, ? extends GameResult> expansionGameState = playoutGameState.getCopy();
	    while (!playoutGameState.isGameOver()) {
	        GM nextMove = playoutPolicy.getPlayoutMove(playoutGameState);
	        playoutGameState.applyMove(nextMove);
	    }
	    nodeResultsService.propagateGameResult(playoutGameState.getGameResult(), iterator);
	    iterator.expandNode(expansionGameState);
	}

	@Override
	public GM selectMove() {
		return searchTree.getMostSimulatedMove();
	}

	@Override
	public void applyMove(GM move, GameState<GM, ? extends GameResult> resultingGameState) {
	    searchTree.advanceTree(move, resultingGameState);
	}
	
	public void logMoveChoices(GameState<GM, ? extends GameResult> rootGameState) {
        String evaluatingPlayerName = rootGameState.getNextPlayerName();
        LOGGER.debug("Top level moves considered by the AIService from {}'s perspective:", evaluatingPlayerName);
        SearchTreeIterator<GM,NR> iterator = searchTree.iterator();
        while (iterator.hasNextChild()) {
            iterator.advanceChildNode();
            String moveString = rootGameState.getHumanReadableMoveString(iterator.getCurrentChildMove());
            NR nodeResults = iterator.getCurrentChildIterator().getCurrentNodeResults();
            LOGGER.debug("Move {} was visited in {} simulations and has score {}", 
                    moveString, 
                    nodeResults.getNumSimulations(),
                    nodeResults.getValue(evaluatingPlayerName));
        }
    }
}
