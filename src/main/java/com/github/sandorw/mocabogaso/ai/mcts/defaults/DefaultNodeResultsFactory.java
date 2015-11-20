package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory for creating DefaultNodeResults objects.
 * 
 * @author sandorw
 */
public final class DefaultNodeResultsFactory implements NodeResultsFactory<DefaultNodeResults> {

    @Override
    public <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            DefaultNodeResults getNewNodeResults(GM move, GS initialGameState) {
        return new DefaultNodeResults(initialGameState);
    }

}
