package com.github.sandorw.mocabogaso.zobrist;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameState;
import com.google.common.collect.Maps;

/**
 * Attempts to check all reachable states within a game to detect Zobrist hash collisions.
 * 
 * @author sandorw
 */
public class HashCollisionChecker {
    private final Logger LOGGER = LoggerFactory.getLogger(HashCollisionChecker.class);
    
    private final Map<Long,GameState<?,?>> zobristHashMap;
    private int numCollisions;
    
    public HashCollisionChecker() {
        zobristHashMap = Maps.newHashMap();
        numCollisions = 0;
    }
    
    public <GM extends GameMove> int detectCollisions(GameState<GM,?> gameState) {
        LOGGER.info("Checking for hash collisions...");
        exploreChildGameStates(gameState);
        LOGGER.info("{} hash collisions found.", numCollisions);
        return numCollisions;
    }
    
    private <GM extends GameMove> void exploreChildGameStates(GameState<GM,?> gameState) {
        long zobristHash = gameState.getZobristHash();
        if (zobristHashMap.containsKey(zobristHash) && !gameState.equals(zobristHashMap.get(zobristHash))) {
            ++numCollisions;
            LOGGER.info("Hash collision found between two distinct game states:");
            LOGGER.info(gameState.toString());
            LOGGER.info(zobristHashMap.get(zobristHash).toString());
        } else {
            zobristHashMap.put(zobristHash, gameState);
        }
        for (GM move : gameState.getAllValidMoves()) {
            GameState<GM,?> newGameState = gameState.getCopy();
            newGameState.applyMove(move);
            exploreChildGameStates(newGameState);
        }
    }
    
}
