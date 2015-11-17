package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree.SearchTreeIterator;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Sets;

/**
 * AI service for managing a Monte Carlo search tree based AI that maintains AMAF results on tree
 * nodes.
 *
 * @author sandorw
 */
public final class AMAFMonteCarloSearchService<GM extends GameMove, NR extends AMAFNodeResults> implements AIService<GM> {
    private static Logger LOGGER = LoggerFactory.getLogger(AMAFMonteCarloSearchService.class);

    private final MonteCarloSearchTree<GM,NR> searchTree;
    private final PlayoutPolicy playoutPolicy;
    private final AMAFNodeResultsService<NR> nodeResultsService;
    
    public <GR extends GameResult, GS extends GameState<GM,GR>> 
            AMAFMonteCarloSearchService(AMAFNodeResultsService<NR> nodeResultsService, PlayoutPolicy policy, GS initialGameState) {
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
    public <GR extends GameResult, GS extends GameState<GM,GR>>
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

    private <GR extends GameResult, GS extends GameState<GM,GR>> void performPlayoutSimulation(GS playoutGameState) {
        SearchTreeIterator<GM,NR> iterator = searchTree.iterator();
        while (iterator.hasNext()) {
            String currentPlayerName = playoutGameState.getNextPlayerName();
            GM move = iterator.advanceToNextExplorationNode(currentPlayerName);
            playoutGameState.applyMove(move);
        }
        GameState<GM, ? extends GameResult> expansionGameState = playoutGameState.getCopy();
        Set<GM> playedMoves = Sets.newHashSet();
        while (!playoutGameState.isGameOver()) {
            GM nextMove = playoutPolicy.getPlayoutMove(playoutGameState);
            playedMoves.add(nextMove);
            playoutGameState.applyMove(nextMove);
        }
        nodeResultsService.propagateGameResultWithAMAF(playoutGameState.getGameResult(), iterator, playedMoves);
        iterator.expandNode(expansionGameState);
    }

    @Override
    public GM selectMove() {
        return searchTree.getMostSimulatedMove();
    }

    @Override
    public <GR extends GameResult, GS extends GameState<GM,GR>> void applyMove(GM move, GS resultingGameState) {
        searchTree.advanceTree(move, resultingGameState);
    }

    public <GR extends GameResult, GS extends GameState<GM,GR>> void logMoveChoices(GS rootGameState) {
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
