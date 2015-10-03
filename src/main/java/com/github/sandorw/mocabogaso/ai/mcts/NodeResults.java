package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * Interface representing the results of simulations games using a specific move in the MCST.
 *
 * @author sandorw
 */
public interface NodeResults {

	float getValue(String evaluatingPlayerName);

    int getNumSimulations();

    <GM extends GameMove, GR extends GameResult> void applyGameResult(GR gameResult);
	
}
