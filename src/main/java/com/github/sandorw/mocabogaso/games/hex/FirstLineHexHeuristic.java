package com.github.sandorw.mocabogaso.games.hex;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.hex.HexGameState.BoardStatus;

public class FirstLineHexHeuristic implements Heuristic<DefaultGameMove, DefaultGameResult> {
    private int weight;
    
    public FirstLineHexHeuristic(int weight) {
        this.weight = weight;
    }
    
    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameResult 
            evaluateMove(DefaultGameMove move, GS resultingGameState) {
        if (move == null) {
            return null;
        }
        HexGameState hexGameState = (HexGameState) resultingGameState;
        int rowIndex = hexGameState.getRowNumber(move.getLocation());
        int colIndex = hexGameState.getColNumber(move.getLocation());
        int boardSize = hexGameState.boardSize;
        if ((rowIndex > 0) && (rowIndex < boardSize-1) && 
                (colIndex > 0) && (colIndex < boardSize-1)) {
            return null;
        }
        boolean hasNeighbor = false;
        if (rowIndex != 0) {
            if (hexGameState.boardLocation[rowIndex-1][colIndex] != BoardStatus.EMPTY) {
                hasNeighbor = true;
            }
            if ((colIndex != 0) && (hexGameState.boardLocation[rowIndex-1][colIndex-1] != BoardStatus.EMPTY)) {
                hasNeighbor = true;
            }
        }
        if (rowIndex != boardSize-1) {
            if (hexGameState.boardLocation[rowIndex+1][colIndex] != BoardStatus.EMPTY) {
                hasNeighbor = true;
            }
            if ((colIndex != boardSize-1) && (hexGameState.boardLocation[rowIndex+1][colIndex+1] != BoardStatus.EMPTY)) {
                hasNeighbor = true;
            }
        }
        if ((colIndex != 0) && (hexGameState.boardLocation[rowIndex][colIndex-1] != BoardStatus.EMPTY)) {
            hasNeighbor = true;
        }
        if ((colIndex != boardSize-1) && (hexGameState.boardLocation[rowIndex][colIndex+1] != BoardStatus.EMPTY)) {
            hasNeighbor = true;
        }
        if (!hasNeighbor) {
            return new DefaultGameResult(hexGameState.nextPlayerName, false);
        }
        return null;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameMove suggestPlayoutMove(GS gameState) {
        return null;
    }

}
