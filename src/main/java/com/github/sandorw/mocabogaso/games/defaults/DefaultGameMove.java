package com.github.sandorw.mocabogaso.games.defaults;

import com.github.sandorw.mocabogaso.games.GameMove;

/**
*
*
* @author sandorw
*/
public final class DefaultGameMove implements GameMove {
   private final String currentPlayer;
   private final int location;

   public DefaultGameMove(String player, int loc) {
       currentPlayer = player;
       location = loc;
   }

   @Override
   public String getPlayer() {
       return currentPlayer;
   }

   public int getLocation() {
       return location;
   }

   @Override
   public String toString() {
       return "DefaultGameMove: [currentPlayer: " + currentPlayer + ", location: " + location + "]";
   }
}
