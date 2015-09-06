package com.github.sandorw.mocabogaso;

import com.github.sandorw.mocabogaso.ai.mcts.DefaultNodeResults;
import com.github.sandorw.mocabogaso.ai.mcts.DefaultNodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.NodeResultsFactory;
import com.github.sandorw.mocabogaso.ai.mcts.PlayoutPolicy;
import com.github.sandorw.mocabogaso.ai.mcts.policies.RandomMovePlayoutPolicy;
import com.github.sandorw.mocabogaso.games.connectx.ConnectXGameState;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameMove;
import com.github.sandorw.mocabogaso.games.defaults.DefaultGameResult;
import com.github.sandorw.mocabogaso.players.AIPlayer;
import com.github.sandorw.mocabogaso.players.MultiThreadedAIPlayer;
import com.github.sandorw.mocabogaso.players.Player;

/**
 *
 *
 * @author sandorw
 */
public class Mocabogaso {

    public static void main(String[] args) {

        /*
        TicTacToeGameState initialGameState = new TicTacToeGameState();
        PlayoutPolicy<DefaultGameMove, DefaultGameResult, TicTacToeGameState> ticTacToePlayoutPolicy = new RandomMovePlayoutPolicy<>();
        NodeResultsFactory<DefaultGameMove, DefaultGameResult, TicTacToeGameState,
                           DefaultNodeResults<DefaultGameMove, DefaultGameResult>> nodeResultsFactory
                           = new DefaultNodeResultsFactory<>();

        Player<DefaultGameMove, TicTacToeGameState> playerX = new AIPlayer<>(ticTacToePlayoutPolicy, nodeResultsFactory, 1.0f);
        Player<DefaultGameMove, TicTacToeGameState> playerO = new AIPlayer<>(ticTacToePlayoutPolicy, nodeResultsFactory, 1.0f);

        Game<DefaultGameMove,TicTacToeGameState> ticTacToeGame = new Game<>(initialGameState);
        ticTacToeGame.addPlayer("X", playerX);
        ticTacToeGame.addPlayer("O", playerO);
        ticTacToeGame.playGame();
        */

        /*
        MNKGameState initialGameState = new MNKGameState(3,3,3);
        PlayoutPolicy<DefaultGameMove, DefaultGameResult, MNKGameState> mNKPlayoutPolicy = new RandomMovePlayoutPolicy<>();
        NodeResultsFactory<DefaultGameMove, DefaultGameResult, MNKGameState, DefaultNodeResults<DefaultGameMove, DefaultGameResult>> nodeResultsFactory
            = new DefaultNodeResultsFactory<>();

        Player<DefaultGameMove, MNKGameState> playerX = new AIPlayer<>(mNKPlayoutPolicy, nodeResultsFactory, 1.0f);
        Player<DefaultGameMove, MNKGameState> playerO = new AIPlayer<>(mNKPlayoutPolicy, nodeResultsFactory, 1.0f);
        Player<DefaultGameMove, MNKGameState> humanPlayer = new HumanPlayer<>();

        Game<DefaultGameMove,MNKGameState> MNKGame = new Game<>(initialGameState);
        MNKGame.addPlayer("X", playerX);
        MNKGame.addPlayer("O", playerO);
        //MNKGame.addPlayer("O", humanPlayer);
        MNKGame.playGame();
         */

        int numGames=0, numXWins=0, numOWins=0;
        for (int i=0; i < 1; ++i) {
            ConnectXGameState initialGameState = new ConnectXGameState(6,7,4);
            PlayoutPolicy<DefaultGameMove, DefaultGameResult, ConnectXGameState> cXPlayoutPolicy = new RandomMovePlayoutPolicy<>();
            NodeResultsFactory<DefaultGameMove, DefaultGameResult, ConnectXGameState, DefaultNodeResults<DefaultGameMove, DefaultGameResult>> nodeResultsFactory
            = new DefaultNodeResultsFactory<>();

            //Player<DefaultGameMove, ConnectXGameState> playerX = new AIPlayer<>(cXPlayoutPolicy, nodeResultsFactory, 1.0f);
            MultiThreadedAIPlayer<DefaultGameMove, DefaultGameResult, ConnectXGameState, DefaultNodeResults<DefaultGameMove, DefaultGameResult>> playerX = new MultiThreadedAIPlayer<>(cXPlayoutPolicy, nodeResultsFactory, 1.0f);
            playerX.setNumThreads(3);
            Player<DefaultGameMove, ConnectXGameState> playerO = new AIPlayer<>(cXPlayoutPolicy, nodeResultsFactory, 1.0f);
            //Player<DefaultGameMove, ConnectXGameState> humanPlayer = new HumanPlayer<>();

            Game<DefaultGameMove,ConnectXGameState> MNKGame = new Game<>(initialGameState);
            MNKGame.addPlayer("X", playerX);
            //MNKGame.addPlayer("O", humanPlayer);
            MNKGame.addPlayer("O", playerO);
            MNKGame.playGame();
            playerX.terminate();
            playerO.terminate();

            ++numGames;
            String winningPlayerName = MNKGame.getGameResult().getWinningPlayer();
            if (winningPlayerName.equals("X"))
                ++numXWins;
            else if (winningPlayerName.equals("O"))
                ++numOWins;
        }
        System.out.println("Number of games: " + numGames + ", number of X wins: " + numXWins + ", number of O wins: " + numOWins);

    }

}