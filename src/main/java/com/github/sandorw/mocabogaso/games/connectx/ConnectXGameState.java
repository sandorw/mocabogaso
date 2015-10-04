package com.github.sandorw.mocabogaso.games.connectx;

import java.util.List;

import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKGameState;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKZobristHashService;
import com.google.common.collect.Lists;

public class ConnectXGameState extends MNKGameState {

    public static ConnectXGameState of(int m, int n, int k) {
        return new ConnectXGameState(m, n, k, new MNKZobristHashService(m,n));
    }
    
    private ConnectXGameState(int m, int n, int k, MNKZobristHashService hashService) {
        super(m, n, k, hashService);
    }
    
    @Override
    public GameState<DefaultGameMove, DefaultGameResult> getCopy() {
        ConnectXGameState copy = new ConnectXGameState(numRows, numCols, goalNumInARow, zobristHashService);
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                copy.boardLocation[i][j] = boardLocation[i][j];
        copy.nextPlayerName = nextPlayerName;
        copy.winningPlayerName = winningPlayerName;
        copy.zobristHash = zobristHash; 
        return copy;
    }
    
    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = Lists.newArrayList();
        for (int j=0; j < numCols; ++j)
            for (int i=0; i < numRows; ++i)
                if (boardLocation[i][j] == BoardStatus.EMPTY) {
                    moveList.add(new DefaultGameMove(nextPlayerName, i*numCols + j));
                    break;
                }
        return moveList;
    }
    
    @Override
    public boolean isValidMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        return super.isValidMove(move) && ((i == 0) || (boardLocation[i-1][j] != BoardStatus.EMPTY));
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        int colNumber = Integer.parseInt(input);
        int rowNumber = 0;
        for (int i=0; i < numRows; ++i)
            if (boardLocation[i][colNumber] == BoardStatus.EMPTY) {
                rowNumber = i;
                break;
            }
        return new DefaultGameMove(nextPlayerName, rowNumber*numCols + colNumber);
    }

}
