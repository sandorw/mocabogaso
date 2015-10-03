package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import java.util.Map;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Maps;

/**
 * Default NodeResults implementation, tracking the number of simulations and wins.
 *
 * @author sandorw
 */
public final class DefaultNodeResults implements NodeResults {
    private final Map<String,Float> playerScoreMap;
    private int numSimulations;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    
    public DefaultNodeResults(GameState<?,?> gameState) {
        playerScoreMap = Maps.newHashMap();
        for (String playerName : gameState.getAllPlayerNames()) {
            playerScoreMap.put(playerName, 0.0f);
        }
        numSimulations = 0;
    }

    @Override
    public int getNumSimulations() {
        return numSimulations;
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        return (numSimulations > 0 ? playerScoreMap.get(evaluatingPlayerName)/numSimulations : 0.0f);
    }
    
    @Override
    public <GM extends GameMove, GR extends GameResult> void applyGameResult(GR gameResult) {
        ++numSimulations;
        if (gameResult.isTie()) {
            for (Map.Entry<String,Float> entry : playerScoreMap.entrySet()) {
                String playerName = entry.getKey();
                playerScoreMap.put(playerName, entry.getValue() + TIE_VALUE);
            }
        } else {
            String winningPlayerName = gameResult.getWinningPlayer();
            playerScoreMap.put(winningPlayerName, playerScoreMap.get(winningPlayerName) + WIN_VALUE);
        }
    }
}
