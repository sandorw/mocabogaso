package com.github.sandorw.mocabogaso.ai.mcts;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFHeuristicNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFHeuristicNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.AMAFNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.amaf.DefaultAMAFNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.defaults.DefaultNodeResultsFactory;
import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.ImmutableList;

/**
 * Tests for NodeResults implementations.
 * 
 * @author sandorw
 */
public final class NodeResultsTests {
    @SuppressWarnings("rawtypes")
    private GameState mockedGameState;
    
    @Before
    public void before() {
        mockedGameState = mock(GameState.class);
        when(mockedGameState.getAllPlayerNames()).thenReturn(ImmutableList.of("Player 1", "Player 2"));
    }
    
    /**
     * DefaultNodeResults tests
     */
    
    @Test
    public void defaultNR_initialStateTest() {
        DefaultNodeResults nodeResults = new DefaultNodeResults(mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @Test
    public void defaultNR_factoryTest() {
        DefaultNodeResultsFactory nodeResultsFactory = new DefaultNodeResultsFactory();
        @SuppressWarnings("unchecked")
        DefaultNodeResults nodeResults = nodeResultsFactory.getNewNodeResults(null, mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @Test
    public void defaultNR_winningSimulationTest() {
        DefaultNodeResults nodeResults = new DefaultNodeResults(mockedGameState);
        winningSimulationTest(nodeResults);
    }
    
    @Test
    public void defaultNR_tiedSimulationTest() {
        DefaultNodeResults nodeResults = new DefaultNodeResults(mockedGameState);
        tiedSimulationTest(nodeResults);
    }
    
    /**
     * DefaultAMAFNodeResults tests
     */
    
    @Test
    public void defaultAMAFNR_initialStateTest() {
        DefaultAMAFNodeResults nodeResults = new DefaultAMAFNodeResults(mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @Test
    public void defaultAMAFNR_factoryTest() {
        DefaultAMAFNodeResultsFactory nodeResultsFactory = new DefaultAMAFNodeResultsFactory();
        @SuppressWarnings("unchecked")
        DefaultAMAFNodeResults nodeResults = nodeResultsFactory.getNewNodeResults(null, mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @Test
    public void defaultAMAFNR_winningSimulationTest() {
        DefaultAMAFNodeResults nodeResults = new DefaultAMAFNodeResults(mockedGameState);
        winningSimulationTest(nodeResults);
    }
    
    @Test
    public void defaultAMAFNR_tiedSimulationTest() {
        DefaultAMAFNodeResults nodeResults = new DefaultAMAFNodeResults(mockedGameState);
        tiedSimulationTest(nodeResults);
    }
    
    @Test
    public void defaultAMAFNR_winningAMAFSimulationTest() {
        DefaultAMAFNodeResults nodeResults = new DefaultAMAFNodeResults(mockedGameState);
        winningAMAFSimulationTest(nodeResults);
    }
    
    @Test
    public void defaultAMAFNR_tiedAMAFSimulationTest() {
        DefaultAMAFNodeResults nodeResults = new DefaultAMAFNodeResults(mockedGameState);
        tiedAMAFSimulationTest(nodeResults);
    }

    /**
     * AMAFHeuristicNodeResults tests
     */
    
    @Test
    public void amafHeuristicNR_initialStateTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_noHeuristicFactoryTest() {
        when(mockedGameState.getHeuristics()).thenReturn(ImmutableList.of());
        AMAFHeuristicNodeResultsFactory nodeResultsFactory = new AMAFHeuristicNodeResultsFactory();
        @SuppressWarnings("unchecked")
        AMAFHeuristicNodeResults nodeResults = nodeResultsFactory.getNewNodeResults(null, mockedGameState);
        initialStateTest(nodeResults);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void amafHeuristicNR_winningHeuristicFactoryTest() {
        @SuppressWarnings("rawtypes")
        Heuristic mockedHeuristic = mock(Heuristic.class);
        GameResult mockedGameResult = mock(GameResult.class);
        GameMove mockedGameMove = mock(GameMove.class);
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        when(mockedGameResult.isTie()).thenReturn(false);
        when(mockedHeuristic.evaluateMove(mockedGameMove, mockedGameState)).thenReturn(mockedGameResult);
        when(mockedHeuristic.getWeight()).thenReturn(2);
        when(mockedGameState.getHeuristics()).thenReturn(ImmutableList.of(mockedHeuristic));
        AMAFHeuristicNodeResultsFactory nodeResultsFactory = new AMAFHeuristicNodeResultsFactory();
        AMAFHeuristicNodeResults nodeResults = nodeResultsFactory.getNewNodeResults(mockedGameMove, mockedGameState);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertTrue(nodeResults.getValue("Player 1") > nodeResults.getValue("Player 2"));
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void amafHeuristicNR_tiedHeuristicFactoryTest() {
        @SuppressWarnings("rawtypes")
        Heuristic mockedHeuristic = mock(Heuristic.class);
        GameResult mockedGameResult = mock(GameResult.class);
        GameMove mockedGameMove = mock(GameMove.class);
        when(mockedGameResult.isTie()).thenReturn(true);
        when(mockedHeuristic.evaluateMove(mockedGameMove, mockedGameState)).thenReturn(mockedGameResult);
        when(mockedHeuristic.getWeight()).thenReturn(2);
        when(mockedGameState.getHeuristics()).thenReturn(ImmutableList.of(mockedHeuristic));
        AMAFHeuristicNodeResultsFactory nodeResultsFactory = new AMAFHeuristicNodeResultsFactory();
        AMAFHeuristicNodeResults nodeResults = nodeResultsFactory.getNewNodeResults(mockedGameMove, mockedGameState);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), nodeResults.getValue("Player 2"), 0.001f);
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    @Test
    public void amafHeuristicNR_winningSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        winningSimulationTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_tiedSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        tiedSimulationTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_winningAMAFSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        winningAMAFSimulationTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_tiedAMAFSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        tiedAMAFSimulationTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_winningVirtualSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        winningAMAFSimulationTest(nodeResults);
    }
    
    @Test
    public void amafHeuristicNR_tiedVirtualSimulationTest() {
        AMAFHeuristicNodeResults nodeResults = new AMAFHeuristicNodeResults(mockedGameState);
        tiedAMAFSimulationTest(nodeResults);
    }
    
    /**
     * Test code
     */
    
    public void initialStateTest(NodeResults nodeResults) {
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), 0.0f, 0.001f);
        assertEquals(nodeResults.getValue("Player 2"), 0.0f, 0.001f);
    }
    
    public void winningSimulationTest(NodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(false);
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        nodeResults.applyGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertTrue(nodeResults.getValue("Player 1") > nodeResults.getValue("Player 2"));
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    public void tiedSimulationTest(NodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(true);
        nodeResults.applyGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 1);
        assertEquals(nodeResults.getValue("Player 1"), nodeResults.getValue("Player 2"), 0.001f);
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    public void winningAMAFSimulationTest(AMAFNodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(false);
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        nodeResults.applyAMAFGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertTrue(nodeResults.getValue("Player 1") > nodeResults.getValue("Player 2"));
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    public void tiedAMAFSimulationTest(AMAFNodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(true);
        nodeResults.applyAMAFGameResult(mockedGameResult);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), nodeResults.getValue("Player 2"), 0.001f);
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    public void winningVirtualSimulationTest(HeuristicNodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(false);
        when(mockedGameResult.getWinningPlayer()).thenReturn("Player 1");
        nodeResults.applyVirtualGameResult(mockedGameResult, 1);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertTrue(nodeResults.getValue("Player 1") > nodeResults.getValue("Player 2"));
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
    
    public void tiedVirtualSimulationTest(HeuristicNodeResults nodeResults) {
        GameResult mockedGameResult = mock(GameResult.class);
        when(mockedGameResult.isTie()).thenReturn(true);
        nodeResults.applyVirtualGameResult(mockedGameResult, 1);
        assertEquals(nodeResults.getNumSimulations(), 0);
        assertEquals(nodeResults.getValue("Player 1"), nodeResults.getValue("Player 2"), 0.001f);
        assertTrue(nodeResults.getValue("Player 1") > 0.0f);
    }
}
