package com.github.sandorw.mocabogaso.games.defaults;

import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * Default GameResult implementation. Stores the winning player's name. Allows for a tie.
 *
 * @author sandorw
 */
public final class DefaultGameResult implements GameResult {
    private final String winningPlayerName;
    private final boolean isTie;
    
    public DefaultGameResult(String winningPlayerName, boolean isTie) {
        this.winningPlayerName = winningPlayerName;
        this.isTie = isTie;
    }
    
    @Override
    public boolean isTie() {
        return isTie;
    }

    @Override
    public String getWinningPlayer() {
        return winningPlayerName;
    }
    
    @Override
    public String toString() {
        return "DefaultGameResult: [" + (isTie ? "Tie" : winningPlayerName + " won") + "]";
    }

}
