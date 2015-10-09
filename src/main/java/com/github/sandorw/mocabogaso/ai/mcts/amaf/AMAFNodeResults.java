package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.Map;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Maps;

/**
 * Node results that include AMAF results - results propagated to nodes that were not visited in
 * the simulation, but share moves with the playout. AMAF results are combined with real results
 * via the RAVE formula.
 * 
 * @author sandorw
 */
public class AMAFNodeResults implements NodeResults {
    private final Map<String,Float> playerScoreMap;
    private final Map<String,Float> amafScoreMap;
    private int numSimulations;
    private int numRAVESimulations;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    private static int SIMS_EQUIV = 1000;
    
    public AMAFNodeResults(GameState<?,?> gameState) {
        playerScoreMap = Maps.newHashMap();
        amafScoreMap = Maps.newHashMap();
        for (String playerName : gameState.getAllPlayerNames()) {
            playerScoreMap.put(playerName, 0.0f);
            amafScoreMap.put(playerName, 0.0f);
        }
        numSimulations = 0;
        numRAVESimulations = 0;
    }

    @Override
    public int getNumSimulations() {
        return numSimulations;
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        if (numSimulations + numRAVESimulations == 0)
            return 0.0f;
        float beta = numRAVESimulations/(numRAVESimulations + numSimulations + (float)numRAVESimulations*numSimulations/SIMS_EQUIV);
        float RAVEterm = (numRAVESimulations == 0 ? 0.0f : beta*amafScoreMap.get(evaluatingPlayerName)/numRAVESimulations);
        float normterm = (numSimulations == 0 ? 0.0f : (1.0f-beta)*playerScoreMap.get(evaluatingPlayerName)/numSimulations);
        return RAVEterm + normterm;
    }
    
    @Override
    public <GR extends GameResult> void applyGameResult(GR gameResult) {
        ++numSimulations;
        applyGameResultToScoreMap(gameResult, playerScoreMap);
    }
    
    public <GM extends GameMove, GR extends GameResult> void applyAMAFGameResult(GR gameResult) {
        ++numRAVESimulations;
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
