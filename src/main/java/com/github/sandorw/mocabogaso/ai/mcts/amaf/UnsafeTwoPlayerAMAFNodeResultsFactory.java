package com.github.sandorw.mocabogaso.ai.mcts.amaf;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory for creating UnsafeTwoPlayerAMAFNodeResults objects.
 * 
 * @author sandorw
 */
public final class UnsafeTwoPlayerAMAFNodeResultsFactory implements NodeResultsFactory<UnsafeTwoPlayerAMAFNodeResults> {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> 
            UnsafeTwoPlayerAMAFNodeResults getNewNodeResults(GM move, GS gameState) {
        return new UnsafeTwoPlayerAMAFNodeResults(gameState);
    }
}
