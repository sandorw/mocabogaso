# mocabogaso

[![Build Status](https://travis-ci.org/sandorw/mocabogaso.svg?branch=develop)](https://travis-ci.org/sandorw/mocabogaso)
[![Coverage Status](https://coveralls.io/repos/sandorw/mocabogaso/badge.svg?branch=develop&service=github)](https://coveralls.io/github/sandorw/mocabogaso?branch=develop)

mocabogaso is a MOnte CArlo BOard GAme SOlver. It uses [Monte Carlo tree search](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search) to run computer controlled opponents for board games. It's designed to be pluggable, allowing multiple types of AI to be added to solve many types of board games.

The main abstractions of mocabogaso:
 - GameState: Full representation of a game at an instant in time.
 - GameMove: Transitions between GameStates.
 - GameResult: The end result of a finished game.
 - AIService: Performs searches and selects moves for AI players.

Games currently implemented:
 - [m,n,k game](https://en.wikipedia.org/wiki/M,n,k-game) (e.g. Tic-tac-toe)
 - [Connect 4](https://en.wikipedia.org/wiki/Connect_Four) (generalized to any board size and number in a row)
 - [Hex](https://en.wikipedia.org/wiki/Hex_(board_game))

Future plans include Go and Chess, along with an AIService better suited for chess and games with evaluation functions (alpha-beta pruned minimax).

### Running the code

You can play a game against the AI via

	./gradlew run

This command executes the main function in Mocabogaso.java, shown below:

	HexGameState gameState = HexGameState.of(9);
        Game<DefaultGameMove, HexGameState> game = new Game<>(gameState);
        game.addPlayer("X", AIPlayerFactory.getNewAMAFAIPlayer(gameState, 5000));
        game.addPlayer("O", new HumanPlayer<>());
        game.playGame();

By default, you'll play a 9x9 game of Hex in which the AI goes first, taking 5 seconds per move. You can input your moves by entering them in the console, formatted as e.g. A9.
