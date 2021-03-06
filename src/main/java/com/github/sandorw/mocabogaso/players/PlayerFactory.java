package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFMonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.policies.RandomMovePlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

/**
 * Factory class that statically creates Players
 * 
 * @author sandorw
 */
public final class PlayerFactory {

    public static <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            Player<GM> getNewAIPlayer(GS initialGameState, int timePerMoveMs) {
        return new AIBuilder<>(initialGameState)
                .withTimePerMove(timePerMoveMs)
                .build();
    }
    
    public static <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
        Player<GM> getNewAMAFAIPlayer(GS initialGameState, int timePerMoveMs) {
        return new AIBuilder<>(initialGameState)
                .withTimePerMove(timePerMoveMs)
                .withAMAF()
                .build();
    }
    
    public static <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> 
            Player<GM> getNewMultiThreadedAMAFAIPlayer(GS initialGameState, int timePerMoveMs, int numThreads) {
        return new AIBuilder<>(initialGameState)
                .withTimePerMove(timePerMoveMs)
                .multithreaded(numThreads)
                .build();
    }
    
    public static <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>>
            Player<GM> getNewAIPlayer(GS initialGameState, PlayerDifficulty difficulty) {
        switch (difficulty) {
        case EASY:
            return getNewAIPlayer(initialGameState, 3000);
        case HARD:
            return getNewMultiThreadedAMAFAIPlayer(initialGameState, 5000, 2);
        default:
            return getNewAMAFAIPlayer(initialGameState, 4000);
        }
    }
    
    public static <GM extends GameMove> Player<GM> getNewHumanPlayer() {
        return new HumanPlayer<>();
    }
    
    public static <GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>>
            Player<GM> getNewAIAssistedHumanPlayer(GS initialGameState) {
        NodeResultsFactory<DefaultAMAFNodeResults> nodeResultsFactory = new DefaultAMAFNodeResultsFactory();
        AMAFNodeResultsService<DefaultAMAFNodeResults> nodeResultsService = new AMAFNodeResultsService<>(nodeResultsFactory);
        PlayoutPolicy policy = new RandomMovePlayoutPolicy();
        AIService<GM> aiService = new AMAFMonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
        return new AIAssistedHumanPlayer<>(aiService);
    }
}
