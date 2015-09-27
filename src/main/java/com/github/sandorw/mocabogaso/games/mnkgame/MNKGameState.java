package com.github.sandorw.mocabogaso.games.mnkgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.google.common.collect.ImmutableList;

/**
 * Representation of the GameState for the m,n,k game - on an m x n board, players take turns
 * making moves, aiming to get k in a row to win.
 * 
 * @author sandorw
 */
public class MNKGameState implements GameState<DefaultGameMove, DefaultGameResult> {
    protected BoardStatus[][] boardLocation;
    protected final int numRows;
    protected final int numCols;
    protected final int goalNumInARow;
    protected String nextPlayerName;
    protected String winningPlayerName;
    
    protected enum BoardStatus {
        EMPTY, X, O;

        @Override
        public String toString() {
            if (this == EMPTY)
                return " ";
            if (this == X)
                return "X";
            return "O";
        }
    }
    
    public MNKGameState(int m, int n, int k) {
        numRows = m;
        numCols = n;
        if ((m < 1) || (n < 1))
            throw new IllegalArgumentException("Number of rows and columns must be greater than zero");
        goalNumInARow = k;
        if ((k > m) && (k > n))
            throw new IllegalArgumentException("Unwinnable game. k must not be greater than the number of rows and columns");
        if (k < 1)
            throw new IllegalArgumentException("k must be greater than zero");
        boardLocation = new BoardStatus[numRows][numCols];
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                boardLocation[i][j] = BoardStatus.EMPTY;
        nextPlayerName = "X";
        winningPlayerName = "";
    }
    
    @Override
    public GameState<DefaultGameMove, DefaultGameResult> getCopy() {
        MNKGameState copy = new MNKGameState(numRows, numCols, goalNumInARow);
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                copy.boardLocation[i][j] = boardLocation[i][j];
        copy.nextPlayerName = nextPlayerName;
        copy.winningPlayerName = winningPlayerName;
        return copy;
    }

    @Override
    public String getNextPlayerName() {
        return nextPlayerName;
    }

    @Override
    public List<String> getAllPlayerNames() {
        return ImmutableList.of("X", "O");
    }

    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = new ArrayList<>();
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocation[i][j] == BoardStatus.EMPTY)
                    moveList.add(new DefaultGameMove(nextPlayerName, i*numCols + j));
        return moveList;
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        int location = Integer.parseInt(input);
        return new DefaultGameMove(nextPlayerName, location);
    }

    @Override
    public boolean isValidMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        return (move.getPlayerName().equals(nextPlayerName)
                && (i >=0) && (i < numRows) && (j >= 0) && (j < numCols)
                && (boardLocation[i][j] == BoardStatus.EMPTY));
    }
    
    protected int getRowNumber(int location) {
        return location/numCols;
    }

    protected int getColNumber(int location) {
        return location%numCols;
    }

    @Override
    public void applyMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        BoardStatus newStatus = (move.getPlayerName().equals("X") ? BoardStatus.X : BoardStatus.O);
        boardLocation[i][j] = newStatus;
        toggleCurrentPlayer();
        updateWinner(move);
    }
    
    protected void toggleCurrentPlayer() {
        nextPlayerName = (nextPlayerName == "X" ? "O" : "X");
    }
    
    protected void updateWinner(DefaultGameMove move) {
        int rowNum = getRowNumber(move.getLocation());
        int colNum = getColNumber(move.getLocation());
        BoardStatus newStatus = (move.getPlayerName().equals("X") ? BoardStatus.X : BoardStatus.O);
        int rowDelta, colDelta;
        
        //Look for goalNumInARow horizontally, vertically, and along the diagonals
        for (int k=0; k < 4; ++k) {
            int tally = 1;
            if (k == 0) {
                rowDelta = 0;
                colDelta = 1;
            } else if (k == 1) {
                rowDelta = 1;
                colDelta = 1;
            } else if (k == 2) {
                rowDelta = 1;
                colDelta = 0;
            } else {
                rowDelta = 1;
                colDelta = -1;
            }
            for (int i=rowNum+rowDelta, j = colNum+colDelta; 
                    (i >= 0) && (j >= 0) && (i < numRows) && (j < numCols); i += rowDelta, j += colDelta) {
                if (boardLocation[i][j] == newStatus)
                    ++tally;
                else
                    break;
            }
            for (int i=rowNum-rowDelta, j = colNum-colDelta; 
                    (i >= 0) && (j >= 0) && (i < numRows) && (j < numCols); i -= rowDelta, j -= colDelta) {
                if (boardLocation[i][j] == newStatus)
                    ++tally;
                else
                    break;
            }
            if (tally >= goalNumInARow) {
                winningPlayerName = move.getPlayerName();
                break;
            }
        }
    }

    @Override
    public boolean isGameOver() {
        if (!winningPlayerName.isEmpty())
            return true;
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocation[i][j] == BoardStatus.EMPTY)
                    return false;
        return true;
    }

    @Override
    public DefaultGameResult getGameResult() {
        if (winningPlayerName.isEmpty())
            return new DefaultGameResult("no one", true);
        return new DefaultGameResult(winningPlayerName, false);
    }
    
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MNKGameState, m=" + numRows + ", n=" + numCols + ", k=" + goalNumInARow + "\n");
        for (int i=numRows-1; i >= 0; --i) {
            for (int j=0; j < numCols; ++j)
                stringBuilder.append(boardLocation[i][j].toString());
            stringBuilder.append("\n");
        }
        for (int i=0; i < numCols; ++i)
            stringBuilder.append(i);
        stringBuilder.append("\n");
        stringBuilder.append("Next player: ").append(nextPlayerName);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        int hash = Arrays.deepHashCode(boardLocation);
        hash = hash*23 + Objects.hash(numRows, numCols, goalNumInARow, nextPlayerName); 
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof MNKGameState))
            return false;
        if (obj == this)
            return true;

        MNKGameState rhs = (MNKGameState) obj;
        if ((numRows != rhs.numRows) || (numCols != rhs.numCols) || (goalNumInARow != rhs.goalNumInARow) ||
                (!nextPlayerName.equals(rhs.nextPlayerName)))
            return false;
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocation[i][j] != rhs.boardLocation[i][j])
                    return false;
        return true;
    }
    
}
