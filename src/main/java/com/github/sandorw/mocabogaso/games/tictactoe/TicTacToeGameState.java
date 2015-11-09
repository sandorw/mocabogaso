package com.github.sandorw.mocabogaso.games.tictactoe;

import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKZobristHashService;
import com.google.common.collect.Lists;

public class TicTacToeGameState extends MNKGameState {

    public static TicTacToeGameState of() {
        return new TicTacToeGameState();
    }
    
    private TicTacToeGameState() {
        super(3, 3, 3, new MNKZobristHashService(3, 3), Lists.newArrayList());
    }
}
