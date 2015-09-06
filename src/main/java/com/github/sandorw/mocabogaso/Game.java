package com.github.sandorw.mocabogaso;

import com.github.sandorw.mocabogaso.games.GameMove;
import com.github.sandorw.mocabogaso.games.GameResult;
import com.github.sandorw.mocabogaso.games.GameState;
import com.github.sandorw.mocabogaso.players.Player;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author sandorw
 */
public final class Game<GM extends GameMove, GS extends GameState<GM, ? extends GameResult>> {

    private final Map<String,Player<GM,GS>> players;
    private GS globalGameState;
    private final List<GM> moveHistory;

    public Game(GS initialGameState) {
        players = Maps.newHashMap();
        globalGameState = initialGameState;
        moveHistory = Lists.newArrayList();
    }

    public void addPlayer(String name, Player<GM,GS> player) {
        players.put(name, player);
    }

    public void playGame() {
        validatePlayers();
        initializePlayers();
        while (!globalGameState.isGameOver()) {
            GM nextMove = getNextMove();
            applyMove(nextMove);
            displayGameState();
        }
        displayGameResult();
    }

    public GameResult getGameResult() {
        return globalGameState.getGameResult();
    }

    private void validatePlayers() {
        List<String> playerNames = globalGameState.getAllPlayerNames();
        for (String name : playerNames)
            if (!players.containsKey(name))
                throw new IllegalStateException("A player named " + name + " is not represented in the players of the game");
    }

    private void initializePlayers() {
        for (Player<GM,GS> player : players.values())
            player.initialize((GS) globalGameState.getCopy());
    }

    private GM getNextMove() {
        String nextPlayer = globalGameState.getNextPlayer();
        GM nextMove = players.get(nextPlayer).chooseMove((GS) globalGameState.getCopy());
        return nextMove;
    }

    private void applyMove(GM move) {
        globalGameState.applyMove(move);
        moveHistory.add(move);
        for (Player<GM,GS> player : players.values())
            player.informOfMove(move, (GS) globalGameState.getCopy());
    }

    private void displayGameState() {
        System.out.println(globalGameState.toString());
    }

    private void displayGameResult() {
        System.out.println(globalGameState.getGameResult().toString());
    }

}
