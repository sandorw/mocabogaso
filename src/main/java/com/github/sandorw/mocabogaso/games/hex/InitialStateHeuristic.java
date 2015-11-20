package com.github.sandorw.mocabogaso.games.hex;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;

/**
 * Heuristic for 
 * 
 * @author sandorw
 */
public class InitialStateHeuristic implements Heuristic<DefaultGameMove, DefaultGameResult> {
    private int weight;
    
    public InitialStateHeuristic(int weight) {
        this.weight = weight;
    }
    
    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameResult 
            evaluateMove(DefaultGameMove move, GS initialGameState) {
        return new DefaultGameResult(null, true);
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameMove suggestPlayoutMove(GS gameState) {
        return null;
    }
}
