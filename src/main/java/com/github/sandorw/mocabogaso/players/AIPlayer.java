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
    private final AIService<GM> aiService;
    private final int allottedTimeMs;
    
    public AIPlayer(AIService<GM> aiService, int allottedTimeMs) {
        this.aiService = aiService;
        this.allottedTimeMs = allottedTimeMs;
    }
    
    @Override
    public <GR extends GameResult, GS extends GameState<GM,GR>> GM chooseNextMove(GS currentGameState) {
        aiService.searchMoves(currentGameState, allottedTimeMs);
        return aiService.selectMove();
    }

    @Override
    public <GR extends GameResult, GS extends GameState<GM,GR>> 
            void informOfMoveMade(GM move, GS resultingGameState) {
        aiService.applyMove(move, resultingGameState);
    }

    @Override
    public void shutdown() {}
}
