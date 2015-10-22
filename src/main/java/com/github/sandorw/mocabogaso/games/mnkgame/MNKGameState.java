package com.github.sandorw.mocabogaso.games.mnkgame;

import java.util.ArrayList;
import java.util.List;

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
    protected long zobristHash;
    protected final MNKZobristHashService zobristHashService;
    
    protected enum BoardStatus {
        X(0),
        O(1),
        EMPTY(2);
        
        private int index;
        
        BoardStatus(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            if (this == EMPTY)
                return " ";
            if (this == X)
                return "X";
            return "O";
        }
    }
    
    public static MNKGameState of(int m, int n, int k) {
        return new MNKGameState(m, n, k, new MNKZobristHashService(m, n));
    }
    
    protected MNKGameState(int m, int n, int k, MNKZobristHashService hashService) {
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
        zobristHashService = hashService;
        zobristHash = 0L;
    }
    
    @Override
    public GameState<DefaultGameMove, DefaultGameResult> getCopy() {
        MNKGameState copy = new MNKGameState(numRows, numCols, goalNumInARow, zobristHashService);
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                copy.boardLocation[i][j] = boardLocation[i][j];
        copy.nextPlayerName = nextPlayerName;
        copy.winningPlayerName = winningPlayerName;
        copy.zobristHash = zobristHash;
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
        try {
            int columnIndex = (int)input.charAt(0) - 65;
            int rowIndex = Integer.parseInt(input.substring(1))-1;
            return new DefaultGameMove(nextPlayerName, rowIndex*numCols + columnIndex);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return new DefaultGameMove(nextPlayerName, -1);
        }
    }
    
    @Override
    public String getHumanReadableMoveString(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        char colChar = (char)('A' + j);
        return String.valueOf(colChar) + (i+1);
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
        zobristHash ^= zobristHashService.getLocationHash(i,j,newStatus.getIndex());
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
    
    @Override
    public long getZobristHash() {
        return zobristHash;
    }
    
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MNKGameState, m=" + numRows + ", n=" + numCols + ", k=" + goalNumInARow + "\n");
        for (int i=numRows-1; i >= 0; --i) {
            stringBuilder.append((char)('A' + i));
            stringBuilder.append(" ");
            for (int j=0; j < numCols; ++j)
                stringBuilder.append(boardLocation[i][j].toString());
            stringBuilder.append("\n");
        }
        stringBuilder.append("  ");
        for (int i=1; i <= numCols; ++i)
            stringBuilder.append(i);
        stringBuilder.append("\n");
        stringBuilder.append("Next player: ").append(nextPlayerName);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (this.getClass() != obj.getClass()))
            return false;
        if (obj == this)
            return true;

        MNKGameState rhs = (MNKGameState) obj;
        if ((getZobristHash() != rhs.getZobristHash()) || (numRows != rhs.numRows) || (numCols != rhs.numCols) || 
                (goalNumInARow != rhs.goalNumInARow) || (!nextPlayerName.equals(rhs.nextPlayerName)))
            return false;
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocation[i][j] != rhs.boardLocation[i][j])
                    return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        //Convert long hash into an int by xoring the high and low bits
        return (int) ((zobristHash >>> 32) ^ ((zobristHash & 0xFFFF0000) >>> 32));
    }
    
}
