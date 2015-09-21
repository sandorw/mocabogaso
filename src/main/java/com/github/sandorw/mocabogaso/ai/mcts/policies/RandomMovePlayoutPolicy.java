package com.github.sandorw.mocabogaso.ai.mcts.policies;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * PlayoutPolicy that chooses randomly among the available moves for a given GameState.
 *
 * @author sandorw
 */
public class RandomMovePlayoutPolicy implements PlayoutPolicy {

    @Override
    public <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> GM getPlayoutMove(GS gameState) {
        List<GM> moveList = gameState.getAllValidMoves();
        int listIndex = ThreadLocalRandom.current().nextInt(moveList.size());
        return moveList.get(listIndex);
    }

}
