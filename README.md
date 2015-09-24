# mocabogaso

[![Build Status](https://travis-ci.org/sandorw/mocabogaso.svg?branch=develop)](https://travis-ci.org/sandorw/mocabogaso)
[![Coverage Status](https://coveralls.io/repos/sandorw/mocabogaso/badge.svg?branch=develop&service=github)](https://coveralls.io/github/sandorw/mocabogaso?branch=develop)

mocabogaso is a MOnte CArlo BOard GAme SOlver. It uses [Monte Carlo tree search](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search) to run computer controlled opponents for board games. It's designed to be pluggable, allowing multiple types of AI to be added to solve many types of board games.

The main abstractions of mocabogaso:
 - GameState: Full representation of a game at an instant in time.
 - GameMove: Transitions between GameStates.
 - GameResult: The end result of a finished game.
 - AIService: Performs searches and selects moves for AI players.
 
