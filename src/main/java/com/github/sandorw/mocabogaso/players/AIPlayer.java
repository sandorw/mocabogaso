package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public class AIPlayer<GM extends GameMove,
                      GR extends GameResult,
                      GS extends GameState<GM,GR>,
                      NR extends NodeResults<GM,GR>> implements Player<GM,GS> {

    protected float timePerMove;
    protected PlayoutPolicy<GM,GR,GS> playoutPolicy;
    protected MonteCarloSearchTree<GM,GR,GS,NR> searchTree;

    public AIPlayer(PlayoutPolicy<GM,GR,GS> policy, NodeResultsFactory<GM,GR,GS,NR> nodeResultsFactory, float timePerMove) {
        playoutPolicy = policy;
        searchTree = new MonteCarloSearchTree<>(nodeResultsFactory);
        this.timePerMove = timePerMove;
    }

    @Override
    public GM chooseMove(GS gameState) {
        long timeout = System.currentTimeMillis() + (long)(timePerMove * 1000);
        int numSimulations = 0;
        while (System.currentTimeMillis() < timeout) {
            searchTree.playSimulationGame((GS) gameState.getCopy(), playoutPolicy);
            ++numSimulations;
        }
        System.out.println("Performed " + numSimulations + " simulations.");
        return searchTree.suggestMove();
    }

    @Override
    public void initialize(GS gameState) {
        searchTree.initialize(gameState);
    }

    @Override
    public void informOfMove(GM move, GS resultingGameState) {
        searchTree.applyMove(move, resultingGameState);
    }

    public void setExplorationConstant(float explorationConstant) {
        searchTree.setExplorationConstant(explorationConstant);
    }

    public void setMaxNodeDepth(int maxNodeDepth) {
        searchTree.setMaxNodeDepth(maxNodeDepth);
    }

    public void setNodeExpandThreshold(int nodeExpandThreshold) {
        searchTree.setNodeExpandThreshold(nodeExpandThreshold);
    }

    @Override
    public void terminate() {}

}
