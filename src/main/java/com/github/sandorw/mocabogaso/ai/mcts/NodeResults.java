package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

/**
 *
 *
 * @author sandorw
 */
public interface NodeResults<GM extends GameMove, GR extends GameResult> {

    public float getValue(GM appliedMove);

    public int getNumSimulations();

    public void applyGameResultWithMove(GR gameResult, GM appliedMove);

}
