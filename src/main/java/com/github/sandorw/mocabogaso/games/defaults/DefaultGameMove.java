package com.github.sandorw.mocabogaso.games.defaults;

import com.github.sandorw.mocabogaso.games.GameMove;

/**
 * Default GameMove implementation. Uses an integer to store information about the move.
 *
 * @author sandorw
 */
public final class DefaultGameMove implements GameMove {
    private final String playerName;
    private final int location;
    
    public DefaultGameMove(String playerName, int location) {
        this.playerName = playerName;
        this.location = location;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }
    
    public int getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "DefaultGameMove: [current player: " + playerName + ", location: " + location + "]";
    }

}
