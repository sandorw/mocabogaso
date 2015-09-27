package com.github.sandorw.mocabogaso.ai.mcts.defaults;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;

/**
 * Test cases for DefaultNodeResults.
 *
 * @author sandorw
 */
public final class DefaultNodeResultsTest {

    private DefaultNodeResults nodeResults;
    private GameResult mockedGameResult;
    private GameMove mockedGameMove;
    
    @Before
    public void before() {
        nodeResults = new DefaultNodeResults();
        mockedGameResult = mock(GameResult.class);
        mockedGameMove = mock(GameMove.class);
    }
    
    @Test
    public void newDefaultNodeResultsStateTest() {
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue(), 0.0f, 0.001f);
    }
    
    @Test
    public void addWinningSimulationTest() {
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        when(mockedGameMove.getPlayerName()).thenReturn("Player 1");
        nodeResults.applyGameResult(mockedGameResult, mockedGameMove);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertTrue(nodeResults.getValue() > 0.0f);
    }
    
    @Test
    public void addLosingSimulationTest() {
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        when(mockedGameMove.getPlayerName()).thenReturn("Player 2");
        when(mockedGameResult.isTie()).thenReturn(false);
        nodeResults.applyGameResult(mockedGameResult, mockedGameMove);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertTrue(nodeResults.getValue() < 0.0f);
    }

    @Test
    public void addTiedSimulationTest() {
        when(mockedGameResult.getWinningPlayer()).thenReturn("No one");
        when(mockedGameMove.getPlayerName()).thenReturn("Player 2");
        when(mockedGameResult.isTie()).thenReturn(true);
        nodeResults.applyGameResult(mockedGameResult, mockedGameMove);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertEquals(nodeResults.getValue(), 0.0f, 0.001f);
    }
}
