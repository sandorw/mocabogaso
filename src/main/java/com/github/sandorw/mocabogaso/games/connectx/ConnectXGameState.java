package com.github.sandorw.mocabogaso.games.connectx;

import java.util.ArrayList;
import java.util.List;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;

/**
 *
 *
 * @author sandorw
 */
public class ConnectXGameState extends MNKGameState {

    public ConnectXGameState(int M, int N, int K) {
        super(M, N, K);
    }

    @Override
    public ConnectXGameState getCopy() {
        ConnectXGameState copy = new ConnectXGameState(numRows, numCols, numInARow);
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                copy.boardLocationStatus[i][j] = boardLocationStatus[i][j];
        copy.currentPlayer = currentPlayer;
        copy.winner = winner;
        return copy;
    }

    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = new ArrayList<>();
        for (int j=0; j < numCols; ++j)
            for (int i=0; i < numRows; ++i)
                if (boardLocationStatus[i][j] == BoardStatus.EMPTY) {
                    moveList.add(new DefaultGameMove(currentPlayer, i*numCols + j));
                    break;
                }
        return moveList;
    }

    @Override
    public boolean isValidMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        return super.isValidMove(move) && ((i == 0) || (boardLocationStatus[i-1][j] != BoardStatus.EMPTY));
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        int colNumber = Integer.parseInt(input);
        int rowNumber = 0;
        for (int i=0; i < numRows; ++i)
            if (boardLocationStatus[i][colNumber] == BoardStatus.EMPTY) {
                rowNumber = i;
                break;
            }
        return new DefaultGameMove(currentPlayer, rowNumber*numCols + colNumber);
    }

}
