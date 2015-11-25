package com.github.sandorw.mocabogaso.players;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFHeuristicNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFHeuristicNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFMonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.RandomMovePlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;

public class AIBuilder<GM extends GameMove, GR extends GameResult, GS extends GameState<GM,GR>> {
    private GS initialGameState;
    private boolean withAMAF;
    private boolean withHeuristics;
    private int timePerMoveMs;
    private int numThreads;
    
    public AIBuilder(GS initialGameState) {
        this.initialGameState = initialGameState;
        withAMAF = false;
        withHeuristics = false;
        timePerMoveMs = 1000;
        numThreads = 1;
    }
    
    public AIBuilder<GM,GR,GS> withAMAF() {
        withAMAF = true;
        return this;
    }
    
    public AIBuilder<GM,GR,GS> withHeuristics() {
        withHeuristics = true;
        return this;
    }
    
    public AIBuilder<GM,GR,GS> withTimePerMove(int timePerMoveMs) {
        this.timePerMoveMs = timePerMoveMs;
        return this;
    }
    
    public AIBuilder<GM,GR,GS> multithreaded(int numThreads) {
        this.numThreads = numThreads;
        return this;
    }
    
    public Player<GM> build() {
        PlayoutPolicy policy = new RandomMovePlayoutPolicy();
        AIService<GM> aiService = null;
        if (withHeuristics) {
            NodeResultsFactory<AMAFHeuristicNodeResults> nodeResultsFactory = new AMAFHeuristicNodeResultsFactory();
            if (withAMAF) {
                AMAFNodeResultsService<AMAFHeuristicNodeResults> nodeResultsService 
                        = new AMAFNodeResultsService<>(nodeResultsFactory);
                aiService = new AMAFMonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
            } else {
                DefaultNodeResultsService<AMAFHeuristicNodeResults> nodeResultsService 
                        = new DefaultNodeResultsService<>(nodeResultsFactory);
                aiService = new MonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
            }
        } else if (withAMAF) {
            NodeResultsFactory<DefaultAMAFNodeResults> nodeResultsFactory = new DefaultAMAFNodeResultsFactory();
            AMAFNodeResultsService<DefaultAMAFNodeResults> nodeResultsService
                    = new AMAFNodeResultsService<>(nodeResultsFactory);
            aiService = new AMAFMonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
        } else {
            NodeResultsFactory<DefaultNodeResults> nodeResultsFactory = new DefaultNodeResultsFactory();
            NodeResultsService<DefaultNodeResults> nodeResultsService 
                    = new DefaultNodeResultsService<>(nodeResultsFactory);
            aiService = new MonteCarloSearchService<>(nodeResultsService, policy, initialGameState);
        }
        if (numThreads > 1) {
            return new MultiThreadedAIPlayer<>(aiService, timePerMoveMs, numThreads);
        }
        return new AIPlayer<>(aiService, timePerMoveMs);
    }
}
