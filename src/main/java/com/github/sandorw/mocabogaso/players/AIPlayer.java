package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Implementation of Player for AI players. Leverages a AIService for move search and generation.
 * 
 * @author sandorw
 */
public final class AIPlayer<GM extends GameMove> implements Player<GM> {
    private AIService<GM> aiService;
    private int allottedTimeMs;
    
    public AIPlayer(AIService<GM> aiService, int allottedTimeMs) {
        this.aiService = aiService;
        this.allottedTimeMs = allottedTimeMs;
    }
    
    @Override
    public <GS extends GameState<GM, ? extends GameResult>> GM chooseNextMove(GS currentGameState) {
        aiService.searchMoves(currentGameState, allottedTimeMs);
        return aiService.selectMove();
    }

    @Override
    public <GS extends GameState<GM, ? extends GameResult>> 
            void informOfMoveMade(GM move, GS resultingGameState) {
        aiService.applyMove(move, resultingGameState);
    }

}
