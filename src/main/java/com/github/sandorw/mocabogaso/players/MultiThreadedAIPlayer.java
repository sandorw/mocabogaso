package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;
import java.util.ArrayList;

/**
 *
 *
 * @author sandorw
 */
public class MultiThreadedAIPlayer<GM extends GameMove,
                                   GR extends GameResult,
                                   GS extends GameState<GM,GR>,
                                   NR extends NodeResults<GM,GR>> extends AIPlayer<GM,GR,GS,NR> {

    int numThreads;
    ArrayList<PlayoutThread<GM,GR,GS,NR>> runnerList;

    public MultiThreadedAIPlayer(PlayoutPolicy<GM,GR,GS> policy, NodeResultsFactory<GM,GR,GS,NR> nodeResultsFactory, float timePerMove) {
        super(policy, nodeResultsFactory, timePerMove);
        numThreads = 1;
        runnerList = Lists.newArrayList();
    }

    @Override
    public void initialize(GS gameState) {
        super.initialize(gameState);
        for (int i=0; i < numThreads; ++i) {
            PlayoutThread<GM,GR,GS,NR> thread = new PlayoutThread<>(playoutPolicy, searchTree);
            thread.start();
            runnerList.add(thread);
        }
    }

    public void setNumThreads(int nThreads) {
        numThreads = nThreads;
    }

    @Override
    public GM chooseMove(GS gameState) {
        long timeout = System.currentTimeMillis() + (long)(timePerMove * 1000);
        for (PlayoutThread<GM,GR,GS,NR> thread : runnerList) {
            thread.updateGameState(gameState);
            thread.unpause();
        }
        while (System.currentTimeMillis() < timeout) {}
        int numSimulations = 0;
        for (PlayoutThread<GM,GR,GS,NR> thread : runnerList) {
            thread.pause();
            numSimulations += thread.getNumSimulations();
        }
        System.out.println("Performed " + numSimulations + " simulations.");
        return searchTree.suggestMove();
    }

    @Override
    public void terminate() {
        for (PlayoutThread<GM,GR,GS,NR> thread : runnerList)
            thread.cancel();
    }

}
