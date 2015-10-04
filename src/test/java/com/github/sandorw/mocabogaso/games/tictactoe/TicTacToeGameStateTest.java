package com.github.sandorw.mocabogaso.games.tictactoe;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sandorw.mocabogaso.Game;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.players.AIPlayerFactory;

public class TicTacToeGameStateTest {

    @Test
    public void playFullGameTest() {
        TicTacToeGameState gameState = TicTacToeGameState.of();
        Game<DefaultGameMove, MNKGameState> game = new Game<>(gameState);
        game.addPlayer("X", AIPlayerFactory.getNewAIPlayer(gameState, 50));
        game.addPlayer("O", AIPlayerFactory.getNewAIPlayer(gameState, 50));
        game.playGame();
        assertTrue(game.isGameOver());
        GameResult gameResult = game.getGameResult();
        assertTrue(gameResult.isTie());
    }

}
