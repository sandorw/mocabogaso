package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Given a GameState, the PlayoutPolicy chooses the next move played by the AI in a simulated game.
 *
 * @author sandorw
 */
public interface PlayoutPolicy {

    <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> GM getPlayoutMove(GS gameState);

}
