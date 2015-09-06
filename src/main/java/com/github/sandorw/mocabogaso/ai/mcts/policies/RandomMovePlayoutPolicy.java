package com.github.sandorw.mocabogaso.ai.mcts.policies;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 *
 *
 * @author sandorw
 */
public final class RandomMovePlayoutPolicy<GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> implements PlayoutPolicy<GM,GR,GS> {

    public RandomMovePlayoutPolicy() {}

    @Override
    public GM getPlayoutMove(GS gameState) {
        List<GM> moveList = gameState.getAllValidMoves();
        int listIndex = ThreadLocalRandom.current().nextInt(moveList.size());
        return moveList.get(listIndex);
    }

}
