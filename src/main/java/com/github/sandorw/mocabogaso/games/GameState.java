package com.github.sandorw.mocabogaso.games;

import java.util.List;

/**
 *
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

    public  void setIsPlayout();

    public  boolean isPlayoutOver();

    public  boolean isGameOver();

    public  GR getGameResult();

}
