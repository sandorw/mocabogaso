package com.github.sandorw.mocabogaso.games.test;

import java.util.List;

import com.github.sandorw.mocabogaso.ai.mcts.Heuristic;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Simple test GameState. Players chose a number from 1 to 3. The player who makes the sum reach 10 loses.
 *
 * @author sandorw
 */
public final class SimpleTestGameState implements GameState<DefaultGameMove, DefaultGameResult> {
    private static int GOAL_SUM = 10;
    private int moveSum;
    private String lastPlayer;
    
    public SimpleTestGameState() {
        moveSum = 0;
        lastPlayer = "Player 2";
    }
    
    @Override
    public GameState<DefaultGameMove, DefaultGameResult> getCopy() {
        SimpleTestGameState copy = new SimpleTestGameState();
        copy.moveSum = moveSum;
        copy.lastPlayer = lastPlayer;
        return copy;
    }

    @Override
    public String getNextPlayerName() {
        return getOppositePlayerName(lastPlayer);
    }
    
    private String getOppositePlayerName(String name) {
        return name.equals("Player 2") ? "Player 1" : "Player 2";
    }

    @Override
    public List<String> getAllPlayerNames() {
        return ImmutableList.of("Player 1", "Player 2");
    }

    @Override
    public List<DefaultGameMove> getAllValidMoves() {
        List<DefaultGameMove> moveList = Lists.newArrayList();
        for (int i=1; moveSum+i <= GOAL_SUM && i <= 3; ++i) {
            moveList.add(new DefaultGameMove(getNextPlayerName(), i));
        }
        return moveList;
    }

    @Override
    public DefaultGameMove getMoveFromString(String input) {
        DefaultGameMove move = new DefaultGameMove(getNextPlayerName(), Integer.parseInt(input));
        if (isValidMove(move))
            return move;
        return null;
    }
    
    @Override
    public String getHumanReadableMoveString(DefaultGameMove move) {
        return String.valueOf(move.getLocation());
    }

    @Override
    public boolean isValidMove(DefaultGameMove move) {
        if (!move.getPlayerName().equals(getNextPlayerName()))
            return false;
        return ((move.getLocation() >= 1) && (move.getLocation() <= 3) 
                && (moveSum + move.getLocation() <= GOAL_SUM));
    }

    @Override
    public void applyMove(DefaultGameMove move) {
        moveSum += move.getLocation();
        togglePlayer();
    }
    
    private void togglePlayer() {
        lastPlayer = getOppositePlayerName(lastPlayer);
    }

    @Override
    public boolean isGameOver() {
        return (moveSum == 10);
    }

    @Override
    public DefaultGameResult getGameResult() {
        return new DefaultGameResult(getOppositePlayerName(lastPlayer), false);
    }
    
    @Override
    public long getZobristHash() {
        return (2L << moveSum) ^ lastPlayer.hashCode();
    }

    public boolean equalsState(Object obj) {
       if (this.getClass() != obj.getClass())
            return false;
        if (obj == this)
            return true;

        SimpleTestGameState rhs = (SimpleTestGameState) obj;
        return ((getZobristHash() == rhs.getZobristHash()) && (moveSum == rhs.moveSum) && 
                (lastPlayer.equals(rhs.lastPlayer)));
    }
    
    public String toString() {
        return "Current sum: " + moveSum + ", next player: " + getOppositePlayerName(lastPlayer);
    }

    @Override
    public List<Heuristic<DefaultGameMove, DefaultGameResult>> getHeuristics() {
        return Lists.newArrayList();
    }

}
