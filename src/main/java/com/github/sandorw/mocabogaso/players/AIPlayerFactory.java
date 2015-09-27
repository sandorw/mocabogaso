package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.RandomMovePlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory class that statically creates AIPlayers
 * 
 * @author sandorw
 */
public final class AIPlayerFactory {

    public static <GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> 
            Player<GM> getNewAIPlayer(GS initialGameState, int timePerMoveMs) {
        DefaultNodeResultsService nodeResultsService = new DefaultNodeResultsService();
        PlayoutPolicy policy = new RandomMovePlayoutPolicy();
        AIService<GM> aiService = new MonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
        return new AIPlayer<>(aiService, timePerMoveMs);
    }
    
}
