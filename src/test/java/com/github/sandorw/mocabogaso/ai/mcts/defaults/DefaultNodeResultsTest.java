package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.ImmutableList;

/**
 * Test cases for DefaultNodeResults.
 *
 * @author sandorw
 */
public final class DefaultNodeResultsTest {
    private DefaultNodeResults nodeResults;
    
    @Before
    public void before() {
        @SuppressWarnings("rawtypes")
        GameState mockedGameState = mock(GameState.class);
        when(mockedGameState.getAllPlayerNames()).thenReturn(ImmutableList.of("Player 1", "Player 2"));
        nodeResults = new DefaultNodeResults(mockedGameState);
    }
    
    @Test
    public void initialStateTest() {
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), 0.0f, 0.001f);
        assertEquals(nodeResults.getValue("Player 2"), 0.0f, 0.001f);
    }
    
    @Test
    public void winningSimulationTest() {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(false);
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        nodeResults.applyGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertTrue(nodeResults.getValue("Player 1") > nodeResults.getValue("Player 2"));
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    @Test
    public void tiedSimulationTest() {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(true);
        nodeResults.applyGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertEquals(nodeResults.getValue("Player 1"), nodeResults.getValue("Player 2"), 0.001f);
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
}
