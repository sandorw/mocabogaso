package com.github.sandorw.mocabogaso.players;

/**
 * Enum representing AIPlayer difficulty levels
 * 
 * @author sandorw
 */
public enum PlayerDifficulty {
    EASY,
    MEDIUM,
    HARD;

    public static PlayerDifficulty fromString(String difficulty) {
        switch (difficulty) {
        case "easy":
            return EASY;
        case "hard":
            return HARD;
        default:
            return MEDIUM;
        }
    }
}
