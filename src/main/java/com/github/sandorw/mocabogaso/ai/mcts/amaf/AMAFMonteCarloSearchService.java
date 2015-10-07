package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Set;

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
public final class AMAFMonteCarloSearchService<GM extends GameMove> implements AIService<GM> {
    private MonteCarloSearchTree<GM,AMAFNodeResults> searchTree;
    private PlayoutPolicy playoutPolicy;
    private AMAFNodeResultsService nodeResultsService;
    
    public <GS extends GameState<GM, ? extends GameResult>> 
            AMAFMonteCarloSearchService(AMAFNodeResultsService nodeResultsService, PlayoutPolicy policy, GS initialGameState) {
        this.nodeResultsService = nodeResultsService;
        playoutPolicy = policy;
        searchTree = new MonteCarloSearchTree<>(nodeResultsService, initialGameState);
    }
    
    @Override
    public <GS extends GameState<GM, ? extends GameResult>>
            void searchMoves(GS currentGameState, int allottedTimeMs) {
        long timeout = System.currentTimeMillis() + (long)allottedTimeMs;
        searchTree.iterator().expandNode(currentGameState);
        while (System.currentTimeMillis() < timeout) {
            performPlayoutSimulation(currentGameState.getCopy());
        }
    }
    
    private <GS extends GameState<GM, ? extends GameResult>> void performPlayoutSimulation(GS playoutGameState) {
        SearchTreeIterator<GM,AMAFNodeResults> iterator = searchTree.iterator();
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
    public void applyMove(GM move, GameState<GM, ? extends GameResult> resultingGameState) {
        searchTree.advanceTree(move, resultingGameState);
    }
}
