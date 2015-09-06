package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public final class DefaultNodeResults<GM extends GameMove, GR extends GameResult> implements NodeResults<GM,GR> {
    private int numSimulations;
    private int numWins;

    public <GS extends GameState<GM,GR>> DefaultNodeResults(GM move, GS gameState) {
        numSimulations = 0;
        numWins = 0;
    }

    @Override
    public float getValue(GM appliedMove) {
        return (numSimulations == 0 ? 0.0f : (float)numWins/numSimulations);
    }

    @Override
    public int getNumSimulations() {
        return numSimulations;
    }

    @Override
    public void applyGameResultWithMove(GR gameResult, GM appliedMove) {
        ++numSimulations;
        if (appliedMove == null)
            return;
        if (gameResult.getWinningPlayer().equals(appliedMove.getPlayer()))
            ++numWins;
        else if (!gameResult.isTie())
            --numWins;
    }

    @Override
    public String toString() {
        return "DefaultNodeResults: [numWins: " + numWins + ", numSimulations: " + numSimulations + "]";
    }

}
