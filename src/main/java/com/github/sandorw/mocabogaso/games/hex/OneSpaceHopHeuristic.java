package com.github.sandorw.mocabogaso.games.hex;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.hex.HexGameState.BoardStatus;

/**
 * Heuristic to encourage one space jump moves that can still be securely connected if needed.
 * 
 * @author sandorw
 */
public class OneSpaceHopHeuristic implements Heuristic<DefaultGameMove, DefaultGameResult> {
    int weight;
    
    public OneSpaceHopHeuristic(int weight) {
        this.weight = weight;
    }
    
    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameResult 
            evaluateMove(DefaultGameMove move, GS initialGameState) {
        if (move == null) {
            return null;
        }
        HexGameState hexGameState = (HexGameState) initialGameState;
        int rowIndex = hexGameState.getRowNumber(move.getLocation());
        int colIndex = hexGameState.getColNumber(move.getLocation());
        int boardSize = hexGameState.boardSize;
        BoardStatus movingPlayer = (move.getPlayerName().equals("X") ? BoardStatus.X : BoardStatus.O);
        if ((rowIndex < boardSize-2) && (colIndex < boardSize-1)
                && (hexGameState.boardLocation[rowIndex+2][colIndex+1] == movingPlayer) 
                && (hexGameState.boardLocation[rowIndex+1][colIndex] == BoardStatus.EMPTY)
                && (hexGameState.boardLocation[rowIndex+1][colIndex+1] == BoardStatus.EMPTY)) {
            return new DefaultGameResult(move.getPlayerName(), false);
        }
        if (rowIndex < boardSize-1) {
            if ((colIndex < boardSize-2)
                    && (hexGameState.boardLocation[rowIndex+1][colIndex+2] == movingPlayer)
                    && (hexGameState.boardLocation[rowIndex+1][colIndex+1] == BoardStatus.EMPTY)
                    && (hexGameState.boardLocation[rowIndex][colIndex+1] == BoardStatus.EMPTY)) {
                return new DefaultGameResult(move.getPlayerName(), false);
            }
            if ((colIndex > 0)
                    && (hexGameState.boardLocation[rowIndex+1][colIndex-1] == movingPlayer)
                    && (hexGameState.boardLocation[rowIndex][colIndex-1] == BoardStatus.EMPTY)
                    && (hexGameState.boardLocation[rowIndex+1][colIndex] == BoardStatus.EMPTY)) {
                return new DefaultGameResult(move.getPlayerName(), false);
            }
        }
        if (rowIndex > 0) {
            if ((colIndex > 1)
                    && (hexGameState.boardLocation[rowIndex-1][colIndex-2] == movingPlayer)
                    && (hexGameState.boardLocation[rowIndex-1][colIndex-1] == BoardStatus.EMPTY)
                    && (hexGameState.boardLocation[rowIndex][colIndex-1] == BoardStatus.EMPTY)) {
                return new DefaultGameResult(move.getPlayerName(), false);
            }
            if ((colIndex < boardSize-1)
                    && (hexGameState.boardLocation[rowIndex-1][colIndex+1] == movingPlayer)
                    && (hexGameState.boardLocation[rowIndex][colIndex+1] == BoardStatus.EMPTY)
                    && (hexGameState.boardLocation[rowIndex-1][colIndex] == BoardStatus.EMPTY)) {
                return new DefaultGameResult(move.getPlayerName(), false);
            }
        }
        if ((rowIndex > 1) && (colIndex > 0)
                && (hexGameState.boardLocation[rowIndex-2][colIndex-1] == movingPlayer)
                && (hexGameState.boardLocation[rowIndex-1][colIndex] == BoardStatus.EMPTY)
                && (hexGameState.boardLocation[rowIndex-1][colIndex-1] == BoardStatus.EMPTY)) {
            return new DefaultGameResult(move.getPlayerName(), false);
        }        
        return null;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameMove suggestPlayoutMove(GS gameState) {
        return null;
    }

}
