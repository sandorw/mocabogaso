package com.github.sandorw.mocabogaso.players;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;

import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.test.SimpleTestGameState;

/**
 * Test cases for HumanPlayer and AIAssistedHumanPlayer
 * 
 * @author sandorw
 */
public final class HumanPlayerTest {
    private Player<DefaultGameMove> player;
    private SimpleTestGameState gameState;
    
    @Before
    public void before() {
        gameState = new SimpleTestGameState();
    }
    
    @Test
    public void chooseNextMoveValidMoveTest() {
        InputStream inputStream = new ByteArrayInputStream("1".getBytes(Charset.forName("UTF-8")));
        System.setIn(inputStream);
        player = PlayerFactory.getNewHumanPlayer();
        DefaultGameMove move = player.chooseNextMove(gameState);
        assertEquals(move.getLocation(), 1);
        System.setIn(System.in);
    }
    
    @Test
    public void chooseNextMoveInvalidMoveTest() {
        InputStream inputStream = new ByteArrayInputStream("4 4 1".getBytes(Charset.forName("UTF-8")));
        System.setIn(inputStream);
        player = PlayerFactory.getNewHumanPlayer();
        DefaultGameMove move = player.chooseNextMove(gameState);
        assertEquals(move.getLocation(), 1);
        System.setIn(System.in);
    }
    
    @Test
    public void aiAssisted_chooseNextMoveValidMoveTest() {
        InputStream inputStream = new ByteArrayInputStream("1".getBytes(Charset.forName("UTF-8")));
        System.setIn(inputStream);
        player = PlayerFactory.getNewAIAssistedHumanPlayer(gameState);
        DefaultGameMove move = player.chooseNextMove(gameState);
        assertEquals(move.getLocation(), 1);
        System.setIn(System.in);
    }

    @Test
    public void aiAssisted_chooseNextMoveInvalidMoveTest() {
        InputStream inputStream = new ByteArrayInputStream("4 4 1".getBytes(Charset.forName("UTF-8")));
        System.setIn(inputStream);
        player = PlayerFactory.getNewAIAssistedHumanPlayer(gameState);
        DefaultGameMove move = player.chooseNextMove(gameState);
        assertEquals(move.getLocation(), 1);
        System.setIn(System.in);
    }

}
