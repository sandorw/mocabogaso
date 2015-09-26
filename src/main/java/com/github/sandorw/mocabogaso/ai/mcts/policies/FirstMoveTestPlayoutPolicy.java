package com.github.sandorw.mocabogaso.ai.mcts.policies;

import java.util.List;

import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Test PlayoutPolicy that returns the first valid move.
 * 
 * @author sandorw
 */
public final class FirstMoveTestPlayoutPolicy implements PlayoutPolicy {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> GM getPlayoutMove(GS gameState) {
        List<GM> moveList = gameState.getAllValidMoves();
        if (moveList.isEmpty())
            return null;
        return gameState.getAllValidMoves().get(0);
    }

}
