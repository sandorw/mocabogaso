package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import java.util.List;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Node results that include AMAF results - results propagated to nodes that were not visited in
 * the simulation, but share moves with the playout. AMAF results are combined with real results
 * via the RAVE formula. These results are specifically designed not to be thread safe, to allow
 * performance at the expense of noisy results.
 * 
 * @author sandorw
 */
public final class UnsafeTwoPlayerAMAFNodeResults implements AMAFNodeResults {
    private final String player1Name;
    private float player1Score;
    private float player2Score;
    private float player1AMAFScore;
    private float player2AMAFScore;
    private int numSims;
    private int numRAVESims;
    
    private static float WIN_VALUE = 1.0f;
    private static float TIE_VALUE = 0.25f;
    private static int SIMS_EQUIV = 1000;
    
    public UnsafeTwoPlayerAMAFNodeResults(GameState<?,?> gameState) {
        List<String> playerNames = gameState.getAllPlayerNames();
        player1Name = playerNames.get(0);
        player1Score = player1AMAFScore = 0.0f;
        player2Score = player2AMAFScore = 0.0f;
        numSims = 0;
        numRAVESims = 0;
    }

    @Override
    public int getNumSimulations() {
        return numSims;
    }
    
    @Override
    public float getValue(String evaluatingPlayerName) {
        float playerScore = (evaluatingPlayerName.equals(player1Name) ? player1Score : player2Score);
        float playerAMAFScore = (evaluatingPlayerName.equals(player1Name) ? player1AMAFScore : player2AMAFScore);
        if (numSims + numRAVESims == 0)
            return 0.0f;
        float beta = numRAVESims/(numRAVESims + numSims + (float)numRAVESims*numSims/SIMS_EQUIV);
        float RAVEterm = (numRAVESims == 0 ? 0.0f : beta*playerAMAFScore/numRAVESims);
        float normterm = (numSims == 0 ? 0.0f : (1.0f-beta)*playerScore/numSims);
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
}
