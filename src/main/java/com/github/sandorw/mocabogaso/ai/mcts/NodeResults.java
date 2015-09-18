package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

public interface NodeResults {

	float getValue();

    int getNumSimulations();

    <GM extends GameMove, GR extends GameResult> void applyGameResult(GR gameResult, GM appliedMove);
	
}
