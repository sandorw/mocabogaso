package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResults;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * Default NodeResults implementation, tracking the number of simulations and wins.
 *
 * @author sandorw
 */
public final class DefaultNodeResults implements NodeResults {
    private int numWins;
    private int numSimulations;
    
    public DefaultNodeResults() {
        numWins = 0;
        numSimulations = 0;
    }
    
    @Override
    public float getValue() {
        return (numSimulations == 0 ? 0.0f : (float)numWins/numSimulations);
    }

    @Override
    public int getNumSimulations() {
        return numSimulations;
    }

    @Override
    public <GM extends GameMove, GR extends GameResult> void applyGameResult(GR gameResult, GM appliedMove) {
        ++numSimulations;
        if (appliedMove == null)
            return;
        if (gameResult.getWinningPlayer().equals(appliedMove.getPlayer()))
            ++numWins;
        else if (!gameResult.isTie())
            --numWins;
    }

}
