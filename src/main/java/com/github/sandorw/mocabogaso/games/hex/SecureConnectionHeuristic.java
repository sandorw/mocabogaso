package com.github.sandorw.mocabogaso.games.hex;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.hex.HexGameState.BoardStatus;

/**
 * Heuristic to incentivize linking groups together when threatened.
 * 
 * @author sandorw
 */
public class SecureConnectionHeuristic implements Heuristic<DefaultGameMove, DefaultGameResult> {
    private int weight;
    
    public SecureConnectionHeuristic(int weight) {
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
        BoardStatus movingPlayer = (move.getPlayerName().equals("X") ? BoardStatus.X : BoardStatus.O);
        boolean nonUrgentConnectionExists = false;
        boolean directOppositeAllies = false;
        int numAlliedGroups = 0;
        int numAlliedPieces = 0;
        int firstAllyIndex = -4;
        HexGameState.Group[] alliedGroups = {null, null, null};
        for (int i=0; i < 6; ++i) {
            int neighborRow = rowIndex + HexGameState.neighborRowDelta.get(i);
            int neighborCol = colIndex + HexGameState.neighborColDelta.get(i);
            if (hexGameState.isIndexInBounds(neighborRow) && hexGameState.isIndexInBounds(neighborCol)) {
                BoardStatus neighborStatus = hexGameState.boardLocation[neighborRow][neighborCol];
                if (neighborStatus == movingPlayer) {
                    ++numAlliedPieces;
                    HexGameState.Group neighborGroup = hexGameState.groups[neighborRow][neighborCol];
                    boolean matchingGroup = false;
                    for (int j=0; j < numAlliedGroups; ++j) {
                        if (neighborGroup == alliedGroups[j]) {
                            matchingGroup = true;
                            break;
                        }
                    }
                    if (!matchingGroup) {
                        alliedGroups[numAlliedGroups] = neighborGroup;
                        ++numAlliedGroups;
                        if (numAlliedGroups == 1) {
                            firstAllyIndex = i;
                        } else if (firstAllyIndex == i-3) {
                            directOppositeAllies = true;
                        }
                    } 
                } else if (neighborStatus == BoardStatus.EMPTY) {
                    int prevIndex = (i == 0 ? 5 : i-1);
                    int nextIndex = (i == 5 ? 0 : i+1);
                    int prevRow = rowIndex + HexGameState.neighborRowDelta.get(prevIndex);
                    int prevCol = colIndex + HexGameState.neighborColDelta.get(prevIndex);
                    int nextRow = rowIndex + HexGameState.neighborRowDelta.get(nextIndex);
                    int nextCol = colIndex + HexGameState.neighborColDelta.get(nextIndex);
                    if (hexGameState.isIndexInBounds(prevRow) 
                            && hexGameState.isIndexInBounds(prevCol)
                            && hexGameState.isIndexInBounds(nextRow)
                            && hexGameState.isIndexInBounds(nextCol)) {
                        BoardStatus prevStatus = hexGameState.boardLocation[prevRow][prevCol];
                        BoardStatus nextStatus = hexGameState.boardLocation[nextRow][nextCol];
                        HexGameState.Group prevGroup = hexGameState.groups[prevRow][prevCol];
                        HexGameState.Group nextGroup = hexGameState.groups[nextRow][nextCol];
                        if ((prevStatus == movingPlayer) && (nextStatus == movingPlayer)
                                && (prevGroup != nextGroup)) {
                            nonUrgentConnectionExists = true;
                        }
                    }
                }
            }
        }
        if ((numAlliedGroups == 3) || ((numAlliedGroups == 2) 
                && ((directOppositeAllies && (numAlliedPieces == 2))
                || !nonUrgentConnectionExists))) {
            return new DefaultGameResult(move.getPlayerName(), false);
        }
        return null;
    }

    @Override
    public <GS extends GameState<DefaultGameMove, DefaultGameResult>> DefaultGameMove suggestPlayoutMove(GS gameState) {
        return null;
    }
}
