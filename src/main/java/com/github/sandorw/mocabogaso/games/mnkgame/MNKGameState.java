package com.github.sandorw.mocabogaso.games.mnkgame;

import java.util.ArrayList;
import java.util.List;

import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;

/**
 *
 *
 * @author sandorw
 */
public class MNKGameState implements GameState<DefaultGameMove,DefaultGameResult> {
    protected BoardStatus[][] boardLocationStatus;
    protected String currentPlayer;
    protected int numRows, numCols, numInARow;
    protected BoardStatus winner;

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

    public MNKGameState(int M, int N, int K) {
        numRows = M;
        numCols = N;
        numInARow = K;
        boardLocationStatus = new BoardStatus[numRows][numCols];
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                boardLocationStatus[i][j] = BoardStatus.EMPTY;
        currentPlayer = "X";
        winner = BoardStatus.EMPTY;
    }

    @Override
    public MNKGameState getCopy() {
        MNKGameState copy = new MNKGameState(numRows, numCols, numInARow);
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                copy.boardLocationStatus[i][j] = boardLocationStatus[i][j];
        copy.currentPlayer = currentPlayer;
        copy.winner = winner;
        return copy;
    }

    @Override
    public String getNextPlayer() {
        return currentPlayer;
    }

    @Override
    public List<String> getAllPlayerNames() {
        final List<String> playerList = new ArrayList<>();
        playerList.add("X");
        playerList.add("O");
        return playerList;
    }

    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = new ArrayList<>();
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocationStatus[i][j] == BoardStatus.EMPTY)
                    moveList.add(new DefaultGameMove(currentPlayer, i*numCols + j));
        return moveList;
    }

    protected int getRowNumber(int location) {
        return location/numCols;
    }

    protected int getColNumber(int location) {
        return location%numCols;
    }

    @Override
    public boolean isValidMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        return (move.getPlayer().equals(currentPlayer)
                && (i >=0) && (i < numRows) && (j >= 0) && (j < numCols)
                && boardLocationStatus[i][j] == BoardStatus.EMPTY);
    }

    @Override
    public void applyMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        BoardStatus newStatus = (move.getPlayer().equals("X") ? BoardStatus.X : BoardStatus.O);
        boardLocationStatus[i][j] = newStatus;
        toggleCurrentPlayer();
        updateWinner(move);
    }

    protected void updateWinner(DefaultGameMove move) {
        int rowNum = getRowNumber(move.getLocation());
        int colNum = getColNumber(move.getLocation());
        BoardStatus newStatus = (move.getPlayer().equals("X") ? BoardStatus.X : BoardStatus.O);
        int rowDelta, colDelta;
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
            for (int i=rowNum+rowDelta, j = colNum+colDelta; (i >= 0) && (j >= 0) && (i < numRows) && (j < numCols); i += rowDelta, j += colDelta) {
                if (boardLocationStatus[i][j] == newStatus)
                    ++tally;
                else
                    break;
            }
            for (int i=rowNum-rowDelta, j = colNum-colDelta; (i >= 0) && (j >= 0) && (i < numRows) && (j < numCols); i -= rowDelta, j -= colDelta) {
                if (boardLocationStatus[i][j] == newStatus)
                    ++tally;
                else
                    break;
            }
            if (tally >= numInARow) {
                winner = newStatus;
                break;
            }
        }
    }

    protected void toggleCurrentPlayer() {
        currentPlayer = (currentPlayer == "X" ? "O" : "X");
    }

    @Override
    public void setIsPlayout() {}

    @Override
    public boolean isPlayoutOver() {
        return isGameOver();
    }

    @Override
    public boolean isGameOver() {
        if (winner != BoardStatus.EMPTY)
            return true;
        for (int i=0; i < numRows; ++i)
            for (int j=0; j < numCols; ++j)
                if (boardLocationStatus[i][j] == BoardStatus.EMPTY)
                    return false;
        return true;
    }

    @Override
    public DefaultGameResult getGameResult() {
        if (winner == BoardStatus.EMPTY)
            return new DefaultGameResult("no one", true);
        String winningPlayerName = (winner == BoardStatus.X ? "X" : "O");
        return new DefaultGameResult(winningPlayerName, false);
    }

    @Override
    public String toString() {
        String returnString = "";
        for (int i=numRows-1; i >= 0; --i) {
            for (int j=0; j < numCols; ++j)
                returnString += boardLocationStatus[i][j].toString();
            returnString += "\n";
        }
        for (int i=0; i < numCols; ++i)
            returnString += i;
        returnString += "\n";
        returnString += "Next player: " + currentPlayer;
        returnString += "\n";
        return returnString;
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        int location = Integer.parseInt(input);
        return new DefaultGameMove(currentPlayer, location);
    }

}
