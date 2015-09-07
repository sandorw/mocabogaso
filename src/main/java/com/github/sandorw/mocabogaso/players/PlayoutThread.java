package com.github.sandorw.mocabogaso.players;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchTree;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public class PlayoutThread<GM extends GameMove,
                           GR extends GameResult,
                           GS extends GameState<GM,GR>,
                           NR extends NodeResults<GM,GR>> extends Thread {
    private PlayoutPolicy<GM,GR,GS> playoutPolicy;
    private MonteCarloSearchTree<GM,GR,GS,NR> searchTree;
    private volatile GS startingGameState;
    private volatile boolean paused;
    private volatile boolean cancelled;
    private AtomicInteger numSimulations;

    public PlayoutThread(PlayoutPolicy<GM,GR,GS> policy, MonteCarloSearchTree<GM,GR,GS,NR> tree) {
        playoutPolicy = policy;
        searchTree = tree;
        startingGameState = null;
        paused = true;
        numSimulations = new AtomicInteger(0);
    }

    @Override
    public void run() {
        while (!cancelled) {
            if (!paused) {
                searchTree.playSimulationGame((GS) startingGameState.getCopy(), playoutPolicy);
                numSimulations.incrementAndGet();
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    cancelled = true;
                }
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        numSimulations.set(0);
        paused = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public void updateGameState(GS gameState) {
        startingGameState = gameState;
    }

    public int getNumSimulations() {
        return numSimulations.get();
    }

}
