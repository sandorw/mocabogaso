package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public final class DefaultNodeResultsFactory<GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> implements NodeResultsFactory<GM,GR,GS,DefaultNodeResults<GM,GR>> {

    @Override
    public DefaultNodeResults<GM,GR> getNewNodeResults(GM move, GS gameState) {
        return new DefaultNodeResults<>(move, gameState);
    }

}

