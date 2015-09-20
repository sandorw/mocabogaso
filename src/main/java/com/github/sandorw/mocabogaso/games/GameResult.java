package com.github.sandorw.mocabogaso.games;

/**
 * Representation of the end result of a game.
 *
 * @author sandorw
 */
public interface GameResult {

    boolean isTie();

    String getWinningPlayer();

}
