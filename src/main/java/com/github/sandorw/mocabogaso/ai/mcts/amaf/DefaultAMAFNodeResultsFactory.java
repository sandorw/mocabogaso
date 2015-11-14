package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory for creating DefaultAMAFNodeResults objects.
 * 
 * @author sandorw
 */
public final class DefaultAMAFNodeResultsFactory implements NodeResultsFactory<DefaultAMAFNodeResults> {
    
    @Override
    public <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            DefaultAMAFNodeResults getNewNodeResults(GM move, GS gameState) {
        return new DefaultAMAFNodeResults(gameState);
    }
}
