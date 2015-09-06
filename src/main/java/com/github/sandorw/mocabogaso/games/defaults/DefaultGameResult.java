package com.github.sandorw.mocabogaso.games.defaults;

import com.github.sandorw.mocabogaso.games.GameResult;

/**
 *
 *
 * @author sandorw
 */
public final class DefaultGameResult implements GameResult {
    private final String winningPlayer;
    private final boolean isTie;

    public DefaultGameResult(String winner, boolean isTie) {
        winningPlayer = winner;
        this.isTie = isTie;
    }

    @Override
    public String getWinningPlayer() {
        return winningPlayer;
    }

    @Override
    public String toString() {
        if (isTie)
            return "Tie.";
        return winningPlayer + " player won.";
    }

    @Override
    public boolean isTie() {
        return isTie;
    }

}
