package com.github.sandorw.mocabogaso.games;

import java.util.List;

/**
 * This represents all state relevant to a given game.
 *
 * @author sandorw
 */
public interface GameState<GM extends GameMove, GR extends GameResult> {

    public GameState<GM, GR> getCopy();

    public String getNextPlayer();

    public List<String> getAllPlayerNames();

    public  List<GM> getAllValidMoves();

    public GM getMoveFromString(String input);

    public boolean isValidMove(GM move);

    public  void applyMove(GM move);

    public  boolean isGameOver();

    public  GR getGameResult();

}
