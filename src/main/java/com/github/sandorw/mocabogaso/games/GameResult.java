package com.github.sandorw.mocabogaso.games;

/**
 * Representation of the end result of a game.
 *
 * @author sandorw
 */
public interface GameResult {

    public boolean isTie();

    public String getWinningPlayer();

}
