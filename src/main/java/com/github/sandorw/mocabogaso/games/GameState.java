package com.github.sandorw.mocabogaso.games;

import java.util.List;

/**
 * This represents all state relevant to a given game.
 *
 * @author sandorw
 */
public interface GameState<GM extends GameMove, GR extends GameResult> {

    GameState<GM, GR> getCopy();

    String getNextPlayerName();

    List<String> getAllPlayerNames();

    List<GM> getAllValidMoves();

    GM getMoveFromString(String input);
    
    String getHumanReadableMoveString(GM move);

    boolean isValidMove(GM move);

    void applyMove(GM move);

    boolean isGameOver();

    GR getGameResult();

    long getZobristHash();
    
}
