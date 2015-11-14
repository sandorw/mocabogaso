package com.github.sandorw.mocabogaso.players;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;

/**
 * Multithreaded AI Player implementation.
 * 
 * @author sandorw
 */
public class MultiThreadedAIPlayer<GM extends GameMove> implements Player<GM> {
    private final AIService<GM> aiService;
    private final int allottedTimeMs;
    private final int numThreads;
    private final ExecutorService executor;
    
    public MultiThreadedAIPlayer(AIService<GM> aiService, int allottedTimeMs, int numThreads) {
        this.aiService = aiService;
        this.allottedTimeMs = allottedTimeMs;
        this.numThreads = numThreads;
        executor = Executors.newFixedThreadPool(numThreads);
    }
    
    @Override
    public <GR extends GameResult, GS extends GameState<GM,GR>> GM chooseNextMove(GS currentGameState) {
        List<Callable<Void>> taskList = Lists.newArrayList();
        for (int i=0; i < numThreads; ++i) {
            taskList.add(new Callable<Void>() {
                public Void call() {
                    aiService.searchMoves(currentGameState, allottedTimeMs);
                    return null;
                }
            });
        }
        try {
            executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return aiService.selectMove();
        }
        return aiService.selectMove();
    }

    @Override
    public <GR extends GameResult, GS extends GameState<GM,GR>> 
            void informOfMoveMade(GM move, GS resultingGameState) {
        aiService.applyMove(move, resultingGameState);
    }

    @Override
    public void shutdown() {
        executor.shutdownNow();
    }
}
