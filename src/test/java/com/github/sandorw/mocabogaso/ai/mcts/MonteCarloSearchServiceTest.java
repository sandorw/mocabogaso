package com.github.sandorw.mocabogaso.ai.mcts;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.FirstMoveTestPlayoutPolicy;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Test cases for MonteCarloSearchService.
 * 
 * @author sandorw
 */
public final class MonteCarloSearchServiceTest {
    
    @Test
    public void searchProducesValidMovesTest() {
        SimpleTestGameState gameState = new SimpleTestGameState();
        DefaultNodeResultsService nodeResultsService = new DefaultNodeResultsService();
        PlayoutPolicy policy = new FirstMoveTestPlayoutPolicy();
        MonteCarloSearchService<DefaultGameMove,DefaultNodeResults> searchService = new MonteCarloSearchService<>(nodeResultsService, policy, gameState);
        searchService.searchMoves(gameState, 50);
        DefaultGameMove suggestedMove = searchService.selectMove();
        searchService.applyMove(suggestedMove, gameState);
        assertTrue(gameState.isValidMove(suggestedMove));
        gameState.applyMove(suggestedMove);        
        suggestedMove = searchService.selectMove();
        assertTrue(gameState.isValidMove(suggestedMove));
    }
    
    @Test
    public void avoidLosingMoveTest() {
        SimpleTestGameState gameState = new SimpleTestGameState();
        gameState.applyMove(new DefaultGameMove("Player 1", 3));
        gameState.applyMove(new DefaultGameMove("Player 2", 3));
        gameState.applyMove(new DefaultGameMove("Player 1", 2));
        DefaultNodeResultsService nodeResultsService = new DefaultNodeResultsService();
        PlayoutPolicy policy = new FirstMoveTestPlayoutPolicy();
        MonteCarloSearchService<DefaultGameMove,DefaultNodeResults> searchService = new MonteCarloSearchService<>(nodeResultsService, policy, gameState);
        searchService.searchMoves(gameState, 50);
        DefaultGameMove suggestedMove = searchService.selectMove();
        assertEquals(suggestedMove.getLocation(), 1);
    }

}
