package com.github.sandorw.mocabogaso.games.tictactoe;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.ai.mcts.MonteCarloSearchService;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsService;
import com.github.sandorw.mocabogaso.ai.mcts.policies.RandomMovePlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.players.AIPlayer;
import com.github.sandorw.mocabogaso.players.Player;
import com.github.sandorw.mocabogaso.zobrist.HashCollisionChecker;

/**
 * Test cases for TicTacToeGameState
 * 
 * @author sandorw
 */
public class TicTacToeGameStateTest {

    @Test
    public void playFullGameTest() {
        TicTacToeGameState gameState = TicTacToeGameState.of();
        Game<DefaultGameMove, MNKGameState> game = new Game<>(gameState);
        NodeResultsFactory<DefaultNodeResults> nodeResultsFactory = new DefaultNodeResultsFactory();
        DefaultNodeResultsService<DefaultNodeResults> nodeResultsService = new DefaultNodeResultsService<>(nodeResultsFactory);
        PlayoutPolicy policy = new RandomMovePlayoutPolicy();
        MonteCarloSearchService<DefaultGameMove, DefaultNodeResults> aiService 
                = new MonteCarloSearchService<>(nodeResultsService, policy, gameState);
        aiService.setNodeExpandThreshold(1);
        Player<DefaultGameMove> XPlayer = new AIPlayer<>(aiService, 100);
        aiService = new MonteCarloSearchService<>(nodeResultsService, policy, gameState);
        aiService.setNodeExpandThreshold(1);
        Player<DefaultGameMove> OPlayer = new AIPlayer<>(aiService, 100);
        game.addPlayer("X", XPlayer);
        game.addPlayer("O", OPlayer);
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isTie());
    }
    
    @Test
    public void noZobristHashCollisionsTest() {
        TicTacToeGameState gameState = TicTacToeGameState.of();
        HashCollisionChecker hashChecker = new HashCollisionChecker();
        assertEquals(hashChecker.detectCollisions(gameState), 0);
    }

}
