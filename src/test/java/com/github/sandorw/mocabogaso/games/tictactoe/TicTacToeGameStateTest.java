package com.github.sandorw.mocabogaso.games.tictactoe;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.players.PlayerFactory;

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
        game.addPlayer("X", PlayerFactory.getNewAIPlayer(gameState, 150));
        game.addPlayer("O", PlayerFactory.getNewAIPlayer(gameState, 150));
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isTie());
    }

}
