package com.github.sandorw.mocabogaso;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.players.Player;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An instance of a game, with globally tracked GameState and Player instances.
 * 
 * @author sandorw
 */
public final class Game<GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> {
    private static Logger LOGGER = LoggerFactory.getLogger(Game.class);
    
    private final Map<String,Player<GM>> players;
    private GS globalGameState;
    private final List<GM> moveHistory;
    
    public Game(GS initialGameState) {
        players = Maps.newHashMap();
        globalGameState = initialGameState;
        moveHistory = Lists.newArrayList();
    }
    
    public void addPlayer(String playerName, Player<GM> player) {
        players.put(playerName, player);
    }
    
    public void playGame() {
        validatePlayers();
        while (!isGameOver()) {
            GM nextMove = getNextMove();
            applyMove(nextMove);
            System.out.println(globalGameState.toString());
        }
        System.out.println(getGameResult().toString());
        LOGGER.info("Game finished. {}", getGameResult().toString());
        shutdown();
    }
    
    private void validatePlayers() {
        List<String> playerNames = globalGameState.getAllPlayerNames();
        List<String> missingPlayers = Lists.newArrayList();
        for (String name : playerNames) {
            if (!players.containsKey(name)) {
                missingPlayers.add(name);
            }
        }
        if (!missingPlayers.isEmpty()) {
            throw new IllegalStateException("Players named " + String.join(", ", missingPlayers) + 
                    " are not represented in the game");
        }
    }
    
    public boolean isGameOver() {
        return globalGameState.isGameOver();
    }
    
    private GM getNextMove() {
        String nextPlayer = globalGameState.getNextPlayerName();
        LOGGER.info("Requesting next move from {}", nextPlayer);
        GM nextMove = players.get(nextPlayer).chooseNextMove(globalGameState.getCopy());
        LOGGER.info("{} selected move {}", nextPlayer, nextMove);
        return nextMove;
    }
    
    private void applyMove(GM move) {
        globalGameState.applyMove(move);
        moveHistory.add(move);
        for (Player<GM> player : players.values()) {
            player.informOfMoveMade(move, globalGameState.getCopy());
        }
    }
    
    public GameResult getGameResult() {
        return globalGameState.getGameResult();
    }
    
    public void shutdown() {
        for (Player<GM> player : players.values()) {
            player.shutdown();
        }
    }
    
}
