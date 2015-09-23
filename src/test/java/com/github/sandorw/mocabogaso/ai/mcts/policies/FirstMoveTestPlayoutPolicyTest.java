package com.github.sandorw.mocabogaso.ai.mcts.policies;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Lists;

/**
 * Test cases for FirstMoveTestPlayoutPolicy
 * 
 * @author sandorw
 */
public class FirstMoveTestPlayoutPolicyTest {
    
    @SuppressWarnings("unchecked")
    @Test
    public void chooseFirstMoveTest() {
        PlayoutPolicy policy = new FirstMoveTestPlayoutPolicy();
        GameMove mockedMove1 = mock(GameMove.class);
        GameMove mockedMove2 = mock(GameMove.class);
        GameMove mockedMove3 = mock(GameMove.class);
        List<GameMove> moveList = Lists.newArrayList();
        moveList.add(mockedMove1);
        moveList.add(mockedMove2);
        moveList.add(mockedMove3);
        GameState<GameMove,GameResult> mockedGameState = (GameState<GameMove,GameResult>) mock(GameState.class);
        when(mockedGameState.getAllValidMoves()).thenReturn(moveList);
        GameMove chosenMove = policy.getPlayoutMove(mockedGameState);
        assertEquals(chosenMove, mockedMove1);
    }

}
