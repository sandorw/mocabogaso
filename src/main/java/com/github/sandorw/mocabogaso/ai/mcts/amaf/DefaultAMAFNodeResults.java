package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.MapMaker;

/**
 * Node results that include AMAF results - results propagated to nodes that were not visited in
 * the simulation, but share moves with the playout. AMAF results are combined with real results
 * via the RAVE formula. Default implementation allows for any number of players and is thread
 * safe.
 * 
 * @author sandorw
 */
public final class DefaultAMAFNodeResults implements AMAFNodeResults {
    private final Map<String,Float> playerScoreMap;
    private final Map<String,Float> amafScoreMap;
    private AtomicInteger numSimulations;
    private AtomicInteger numRAVESimulations;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    private static int SIMS_EQUIV = 100;
    
    public DefaultAMAFNodeResults(GameState<?,?> gameState) {
        playerScoreMap = new MapMaker()
                .concurrencyLevel(4)
                .initialCapacity(4)
                .makeMap();
        amafScoreMap = new MapMaker()
                .concurrencyLevel(4)
                .initialCapacity(4)
                .makeMap();
        for (String playerName : gameState.getAllPlayerNames()) {
            playerScoreMap.put(playerName, 0.0f);
            amafScoreMap.put(playerName, 0.0f);
        }
        numSimulations = new AtomicInteger(0);
        numRAVESimulations = new AtomicInteger(0);
    }

    @Override
    public int getNumSimulations() {
        return numSimulations.get();
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        int numSims = numSimulations.get();
        int numRAVESims = numRAVESimulations.get();
        if (numSims + numRAVESims == 0)
            return 0.0f;
        float beta = numRAVESims/(numRAVESims + numSims + (float)numRAVESims*numSims/SIMS_EQUIV);
        float RAVEterm = (numRAVESims == 0 ? 0.0f : beta*amafScoreMap.get(evaluatingPlayerName)/numRAVESims);
        float normterm = (numSims == 0 ? 0.0f : (1.0f-beta)*playerScoreMap.get(evaluatingPlayerName)/numSims);
        return RAVEterm + normterm;
    }
    
    @Override
    public <GR extends GameResult> void applyGameResult(GR gameResult) {
        numSimulations.incrementAndGet();
        applyGameResultToScoreMap(gameResult, playerScoreMap);
    }
    
    public <GM extends GameMove, GR extends GameResult> void applyAMAFGameResult(GR gameResult) {
        numRAVESimulations.incrementAndGet();
        applyGameResultToScoreMap(gameResult, amafScoreMap);
    }
    
    private <GR extends GameResult> void applyGameResultToScoreMap(GR gameResult, Map<String,Float> scoreMap) {
        if (gameResult.isTie()) {
            scoreMap.forEach((name, score) -> scoreMap.put(name, score + TIE_VALUE));
        } else {
            String winningPlayerName = gameResult.getWinningPlayer();
            scoreMap.put(winningPlayerName, scoreMap.get(winningPlayerName) + WIN_VALUE);
        }
    }
}
