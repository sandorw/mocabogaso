package com.github.sandorw.mocabogaso.games.hex;

import java.util.List;

import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.games.mnkgame.MNKZobristHashService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * GameState implementation for the board game Hex.
 * 
 * @author sandorw
 */
public final class HexGameState implements GameState<DefaultGameMove, DefaultGameResult> {
    protected BoardStatus[][] boardLocation;
    protected Group[][] groups;
    protected final int boardSize;
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
    
    public static HexGameState of(int boardSize) {
        return new HexGameState(boardSize, new MNKZobristHashService(boardSize, boardSize));
    }
    
    private HexGameState(int boardSize, MNKZobristHashService hashService) {
        if (boardSize < 2)
            throw new IllegalArgumentException("Board size must be greater than 1");
        this.boardSize = boardSize;
        boardLocation = new BoardStatus[boardSize][boardSize];
        groups = new Group[boardSize][boardSize];
        for (int i=0; i < boardSize; ++i)
            for (int j=0; j < boardSize; ++j)
                boardLocation[i][j] = BoardStatus.EMPTY;
        nextPlayerName = "X";
        winningPlayerName = "";
        zobristHash = 0L;
        zobristHashService = hashService;
    }
    
    @Override
    public GameState<DefaultGameMove, DefaultGameResult> getCopy() {
        HexGameState copy = new HexGameState(boardSize, zobristHashService);
        for (int i=0; i < boardSize; ++i) {
            for (int j=0; j < boardSize; ++j) {
                copy.boardLocation[i][j] = boardLocation[i][j];
            }
        }
        for (int i=0; i < boardSize; ++i) {
            for (int j=0; j < boardSize; ++j) {
                if ((groups[i][j] != null) && (copy.groups[i][j] == null)) {
                    Group existingGroup = groups[i][j];
                    Group copiedGroup = new Group(existingGroup);
                    for (int k=0; k < boardSize; ++k) {
                        for (int m=0; m < boardSize; ++m) {
                            if (groups[k][m] == existingGroup) {
                                copy.groups[k][m] = copiedGroup;
                            }
                        }
                    }
                }
            }
        }
        copy.nextPlayerName = nextPlayerName;
        copy.zobristHash = zobristHash;
        return copy;
    }

    @Override
    public String getNextPlayerName() {
        return nextPlayerName;
    }

    @Override
    public List<String> getAllPlayerNames() {
        return ImmutableList.of("X","O");
    }

    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = Lists.newArrayList();
        for (int i=0; i < boardSize; ++i) {
            for (int j=0; j < boardSize; ++j) {
                if (boardLocation[i][j] == BoardStatus.EMPTY) {
                    moveList.add(new DefaultGameMove(nextPlayerName, i*boardSize + j));
                }
            }
        }
        return moveList;
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        try {
            int columnIndex = (int)input.charAt(0) - 65;
            int rowIndex = Integer.parseInt(input.substring(1))-1;
            return new DefaultGameMove(nextPlayerName, rowIndex*boardSize + columnIndex);
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
        if ((i < 0) || (j < 0) || (i >= boardSize) || (j >= boardSize) || 
                (!move.getPlayerName().equals(nextPlayerName)))
            return false;
        return boardLocation[i][j] == BoardStatus.EMPTY;
    }
    
    private int getRowNumber(int location) {
        return location/boardSize;
    }

    private int getColNumber(int location) {
        return location%boardSize;
    }

    @Override
    public void applyMove(DefaultGameMove move) {
        int i = getRowNumber(move.getLocation());
        int j = getColNumber(move.getLocation());
        BoardStatus newStatus = (move.getPlayerName().equals("X") ? BoardStatus.X : BoardStatus.O);
        boardLocation[i][j] = newStatus;
        groups[i][j] = new Group(i,j,newStatus);
        checkAndJoinNeighboringGroups(i,j,i+1,j);
        checkAndJoinNeighboringGroups(i,j,i+1,j+1);
        checkAndJoinNeighboringGroups(i,j,i,j-1);
        checkAndJoinNeighboringGroups(i,j,i,j+1);
        checkAndJoinNeighboringGroups(i,j,i-1,j-1);
        checkAndJoinNeighboringGroups(i,j,i-1,j);
        zobristHash ^= zobristHashService.getLocationHash(i,j,newStatus.getIndex());
        toggleCurrentPlayer();
    }
    
    private void checkAndJoinNeighboringGroups(int i, int j, int neighborRow, int neighborCol) {
        if ((neighborRow >= 0) && (neighborRow < boardSize) && 
                (neighborCol >= 0) && (neighborCol < boardSize) &&
                (boardLocation[i][j] == boardLocation[neighborRow][neighborCol])) {
            addAllToGroup(groups[i][j], groups[neighborRow][neighborCol]);
        }
    }
    
    private void addAllToGroup(Group oldGroup, Group newGroup) {
        for (int i=0; i < boardSize; ++i) {
            for (int j=0; j < boardSize; ++j) {
                if (groups[i][j] == oldGroup) {
                    groups[i][j] = newGroup;
                    if (newGroup.addToGroupAndCheckWin(i,j,boardLocation[i][j])) {
                        winningPlayerName = boardLocation[i][j].toString();
                    }
                }
            }
        }
    }
    
    private void toggleCurrentPlayer() {
        nextPlayerName = (nextPlayerName == "X" ? "O" : "X");
    }

    @Override
    public boolean isGameOver() {
        return !winningPlayerName.isEmpty();
    }

    @Override
    public DefaultGameResult getGameResult() {
        if (isGameOver()) {
            return new DefaultGameResult(winningPlayerName, false);
        }
        throw new IllegalStateException("HexGameState is not finished");
    }

    @Override
    public long getZobristHash() {
        return zobristHash;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HexGameState, board size=" + boardSize + "\n");
        stringBuilder.append("  ");
        for (int i=0; i < boardSize; ++i) {
            stringBuilder.append((char)(i+65) + " ");
        }
        stringBuilder.append("\n");
        for (int i=boardSize-1; i >= 0; --i) {
            for (int k=boardSize-1; k > i; --k) {
                stringBuilder.append(" ");
            }
            if (i+1 < 10)
                stringBuilder.append(" ");
            stringBuilder.append(i+1 + " ");
            for (int j=0; j < boardSize; ++j) {
                stringBuilder.append(boardLocation[i][j] + " ");
            }
            stringBuilder.append("\n");
        }
        for (int i=0; i < boardSize+3; ++i)
            stringBuilder.append(" ");
        for (int i=0; i < boardSize; ++i) {
            stringBuilder.append((char)(i+65) + " ");
        }
        stringBuilder.append("\n" + "Next player: " + nextPlayerName);        
        return stringBuilder.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (this.getClass() != obj.getClass()))
            return false;
        if (obj == this)
            return true;

        HexGameState rhs = (HexGameState) obj;
        if ((getZobristHash() != rhs.getZobristHash()) || (boardSize != rhs.boardSize) ||
                (!nextPlayerName.equals(rhs.nextPlayerName)))
            return false;
        for (int i=0; i < boardSize; ++i)
            for (int j=0; j < boardSize; ++j)
                if (boardLocation[i][j] != rhs.boardLocation[i][j])
                    return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        //Convert long hash into an int by xoring the high and low bits
        return (int) ((zobristHash >>> 32) ^ ((zobristHash & 0xFFFF0000) >>> 32));
    }

    private final class Group {
        private int minBound;
        private int maxBound;
        
        private Group(int i, int j, BoardStatus type) {
            if (type == BoardStatus.X) {
                minBound = maxBound = j;
            } else {
                minBound = maxBound = i;
            }
        }
        
        private Group(Group other) {
            minBound = other.minBound;
            maxBound = other.maxBound;
        }
        
        private boolean addToGroupAndCheckWin(int i, int j, BoardStatus type) {
            if (type == BoardStatus.X) {
                minBound = Math.min(j, minBound);
                maxBound = Math.max(j, maxBound);
            } else {
                minBound = Math.min(i, minBound);
                maxBound = Math.max(i, maxBound);
            }
            return ((minBound == 0) && (maxBound == boardSize-1));
        }
    }
}
