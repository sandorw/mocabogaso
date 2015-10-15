package com.github.sandorw.mocabogaso;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.sandorw.mocabogaso.ai.AIService;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.FirstMoveTestPlayoutPolicy;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;
import com.github.sandorw.mocabogaso.players.AIPlayer;
import com.github.sandorw.mocabogaso.players.Player;

/**
 * Test cases for Game
 * 
 * @author sandorw
 */
public class GameTest {
    private SimpleTestGameState gameState;
    private Game<DefaultGameMove, SimpleTestGameState> game;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Before
    public void before() {
        gameState = new SimpleTestGameState();
        game = new Game<>(gameState);
    }
    
    @Test
    public void missingPlayerTest() {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Players named Player 1, Player 2 are not represented in the game");
        game.playGame();
    }

    @Test
    public void successfulGameCompletionTest() {
        NodeResultsFactory<DefaultNodeResults> nodeResultsFactory = new DefaultNodeResultsFactory();
        DefaultNodeResultsService<DefaultNodeResults> nodeResultsService = new DefaultNodeResultsService<>(nodeResultsFactory);
        PlayoutPolicy policy = new FirstMoveTestPlayoutPolicy();
        AIService<DefaultGameMove> aiService = 
                new MonteCarloSearchService<>(nodeResultsService, policy, gameState);
        Player<DefaultGameMove> player1 = new AIPlayer<>(aiService, 50);
        Player<DefaultGameMove> player2 = new AIPlayer<>(aiService, 50);
        game.addPlayer("Player 1", player1);
        game.addPlayer("Player 2", player2);
        game.playGame();
        assertTrue(game.isGameOver());
        assertFalse(game.getGameResult().isTie());
    }
    
}
