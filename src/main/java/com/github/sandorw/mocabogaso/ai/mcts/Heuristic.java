package com.github.sandorw.mocabogaso.ai.mcts;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Interface representing game heuristics. A heuristic helps with evaluating new moves or
 * suggesting moves for an AI during a playout.
 * 
 * @author sandorw
 */
public interface Heuristic<GM extends GameMove, GR extends GameResult> {

    int getWeight();

    <GS extends GameState<GM,GR>> GR evaluateMove(GM move, GS initialGameState);
    
    <GS extends GameState<GM,GR>> GM suggestPlayoutMove(GS gameState);
    
}
