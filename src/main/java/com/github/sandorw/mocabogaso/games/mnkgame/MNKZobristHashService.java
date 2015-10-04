package com.github.sandorw.mocabogaso.games.mnkgame;

import java.util.Random;

public class MNKZobristHashService {
    private long positionHash[][][];
    
    public MNKZobristHashService(int numRows, int numCols) {
        if ((numRows < 1) || (numCols < 1))
            throw new IllegalArgumentException("Number of rows and columns must be greater than zero");
        Random rng = new Random(0L);
        positionHash = new long[numRows][numCols][2];
        for (int i=0; i < numRows; ++i) {
            for (int j=0; j < numCols; ++j) {
                for (int k=0; k < 2; ++k) {
                    positionHash[i][j][k] = rng.nextLong();
                }
            }
        }
    }
    
    public long getLocationHash(int i, int j, int boardStatusIndex) {
        return positionHash[i][j][boardStatusIndex];
    }
}
