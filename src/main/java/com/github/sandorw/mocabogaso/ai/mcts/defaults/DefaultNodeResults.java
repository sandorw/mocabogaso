package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.MapMaker;

/**
 * Default NodeResults implementation, tracking the number of simulations and wins.
 *
 * @author sandorw
 */
public final class DefaultNodeResults implements NodeResults {
    private final Map<String,Float> playerScoreMap;
    private AtomicInteger numSimulations;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    
    public DefaultNodeResults(GameState<?,?> gameState) {
        playerScoreMap = new MapMaker()
                .concurrencyLevel(4)
                .initialCapacity(4)
                .makeMap();
        for (String playerName : gameState.getAllPlayerNames()) {
            playerScoreMap.put(playerName, 0.0f);
        }
        numSimulations = new AtomicInteger(0);
    }

    @Override
    public int getNumSimulations() {
        return numSimulations.get();
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        return (numSimulations.get() > 0 ? playerScoreMap.get(evaluatingPlayerName)/numSimulations.get() : 0.0f);
    }
    
    @Override
    public <GR extends GameResult> void applyGameResult(GR gameResult) {
        numSimulations.incrementAndGet();
        if (gameResult.isTie()) {
            playerScoreMap.forEach((name, score) -> playerScoreMap.put(name, score + TIE_VALUE));
        } else {
            String winningPlayerName = gameResult.getWinningPlayer();
            playerScoreMap.put(winningPlayerName, playerScoreMap.get(winningPlayerName) + WIN_VALUE);
        }
    }
}
