package com.github.sandorw.mocabogaso.players;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.FirstMoveTestPlayoutPolicy;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Tests cases for AIPlayer
 * 
 * @author sandorw
 */
public final class AIPlayerTest {
    private SimpleTestGameState gameState;
    private AIPlayer<DefaultGameMove> aiPlayer;
    
    @Before
    public void before() {
        DefaultNodeResultsService nodeResultsService = new DefaultNodeResultsService();
        PlayoutPolicy policy = new FirstMoveTestPlayoutPolicy();
        gameState = new SimpleTestGameState();
        AIService<DefaultGameMove> aiService = 
                new MonteCarloSearchService<DefaultGameMove,DefaultNodeResults>(nodeResultsService, policy, gameState);
        aiPlayer = new AIPlayer<DefaultGameMove>(aiService, 50);
    }
    
    @Test
    public void chooseValidMoveTest() {
        DefaultGameMove move = aiPlayer.chooseNextMove(gameState);
        assertTrue(gameState.isValidMove(move));
    }
    
    @Test
    public void applyMoveAndChooseValidMoveTest() {
        DefaultGameMove move = aiPlayer.chooseNextMove(gameState);
        gameState.applyMove(move);
        aiPlayer.informOfMoveMade(move, gameState);
        move = aiPlayer.chooseNextMove(gameState);
        assertTrue(gameState.isValidMove(move));
    }
}
