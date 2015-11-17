package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.List;

import com.github.sandorw.mocabogaso.ai.mcts.HeuristicNodeResults;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * NodeResults that include AMAF data and take heuristics into account when constructed.
 * 
 * @author sandorw
 */
public class AMAFHeuristicNodeResults implements AMAFNodeResults, HeuristicNodeResults {
    private final String player1Name;
    private float player1Score;
    private float player2Score;
    private float player1VirtScore;
    private float player2VirtScore;
    private float player1AMAFScore;
    private float player2AMAFScore;
    private int numSims;
    private int numVirtSims;
    private int numRAVESims;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    private static int SIMS_EQUIV = 1000;
    
    public AMAFHeuristicNodeResults(GameState<?,?> gameState) {
        List<String> playerNames = gameState.getAllPlayerNames();
        player1Name = playerNames.get(0);
        player1Score = player1VirtScore = player1AMAFScore = 0.0f;
        player2Score = player2VirtScore = player2AMAFScore = 0.0f;
        numSims = 0;
        numVirtSims = 0;
        numRAVESims = 0;
    }

    @Override
    public int getNumSimulations() {
        return numSims;
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        int numTotalSims = numSims + numVirtSims;
        if ((numTotalSims == 0) && (numRAVESims == 0)) {
            return 0.0f;
        }
        float playerScore = (evaluatingPlayerName.equals(player1Name) ? player1Score : player2Score);
        float playerVirtScore = (evaluatingPlayerName.equals(player1Name) ? player1VirtScore : player2VirtScore);
        float playerAMAFScore = (evaluatingPlayerName.equals(player1Name) ? player1AMAFScore : player2AMAFScore);
        float weightedScore = (numTotalSims == 0 ? 0.0f : (playerScore + playerVirtScore)/numTotalSims);
        float beta = numRAVESims/(numRAVESims + numTotalSims + (float)numRAVESims*numTotalSims/SIMS_EQUIV);
        float RAVEterm = (numRAVESims == 0 ? 0.0f : beta*playerAMAFScore/numRAVESims);
        float normterm = (1.0f-beta)*weightedScore;
        return RAVEterm + normterm;
    }
    
    @Override
    public <GR extends GameResult> void applyGameResult(GR gameResult) {
        ++numSims;
        if (gameResult.isTie()) {
            player1Score += TIE_VALUE;
            player2Score += TIE_VALUE;
        } else {
            if (gameResult.getWinningPlayer().equals(player1Name)) {
                player1Score += WIN_VALUE;
            } else {
                player2Score += WIN_VALUE;
            }
        }
    }
    
    @Override
    public <GM extends GameMove, GR extends GameResult> void applyAMAFGameResult(GR gameResult) {
        ++numRAVESims;
        if (gameResult.isTie()) {
            player1AMAFScore += TIE_VALUE;
            player2AMAFScore += TIE_VALUE;
        } else {
            if (gameResult.getWinningPlayer().equals(player1Name)) {
                player1AMAFScore += WIN_VALUE;
            } else {
                player2AMAFScore += WIN_VALUE;
            }
        }
    }

    @Override
    public <GR extends GameResult> void applyVirtualGameResult(GR gameResult, int weight) {
        if (weight == 0) {
            return;
        }
        numVirtSims += weight;
        if (gameResult.isTie()) {
            player1VirtScore += weight*TIE_VALUE;
            player2VirtScore += weight*TIE_VALUE;
        } else {
            if (gameResult.getWinningPlayer().equals(player1Name)) {
                player1VirtScore += weight*WIN_VALUE;
            } else {
                player2VirtScore += weight*WIN_VALUE;
            }
        }
    }
}
