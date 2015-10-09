package com.github.sandorw.mocabogaso;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.hex.HexGameState;
import com.github.sandorw.mocabogaso.players.AIPlayerFactory;
import com.github.sandorw.mocabogaso.players.HumanPlayer;

public class Mocabogaso {

    public static void main(String args[]) {
        HexGameState gameState = HexGameState.of(9);
        Game<DefaultGameMove, HexGameState> game = new Game<>(gameState);
        game.addPlayer("X", AIPlayerFactory.getNewAMAFAIPlayer(gameState, 5000));
        game.addPlayer("O", new HumanPlayer<>());
        game.playGame();
    }
}
