package com.github.se.gatherspot.model

enum class Interests {
  SPORT,
  FOOTBALL,
  BASKETBALL,
  TENNIS,
  BOARD_GAMES,
  CHESS,
  ROLE_PLAY,
  VIDEO_GAMES,
  NIGHTLIFE,
  CONCERTS,
  TECHNOLOGY,
  NETWORKING,
  SPEED_DATING,
  ART,
  TRAVEL,
  LEISURE,
  BOWLING;

  // This companion object contains utility functions for working with Interests
  companion object {
    /** The list of all Interests */
    fun toList(): List<Interests> {
      return entries
    }

    /**
     * Returns a new empty set of Interests
     *
     * @return the empty set of Interests
     */
    fun new(): Set<Interests> {
      return setOf()
    }

    /**
     * Converts a compressed string representation of Interests to a set of Interests
     *
     * @param interests the compressed string representation
     * @return the set of Interests
     */
    fun fromCompressedString(interests: String): Set<Interests> {
      val set = mutableSetOf<Interests>()
      interests.forEachIndexed { index, c -> if (c == '1') set.add(entries[index]) }
      return set.toSet()
    }

    /**
     * Converts a set of Interests to a compressed string representation to be stored in the
     * database
     *
     * @param interests the set of Interests to convert
     * @return the compressed string representation
     */
    fun toCompressedString(interests: Set<Interests>): String {
      return entries.joinToString("") { interest -> if (interests.contains(interest)) "1" else "0" }
    }

    /**
     * Returns a new set of Interests with the given Interest added
     *
     * @param set the set of Interests
     * @param interest the Interest to add
     * @return the new set of Interests
     */
    fun addInterest(set: Set<Interests>, interest: Interests): Set<Interests> {
      return set.plus(interest)
    }

    /**
     * Returns a new set of Interests with the given Interest removed done in a functional way to be
     * able to use it in a mutableState or LiveData
     *
     * @param set the set of Interests
     * @param interest the Interest to remove
     * @return the new set of Interests
     */
    fun removeInterest(set: Set<Interests>, interest: Interests): Set<Interests> {
      return set.minus(interest)
    }

    /**
     * Returns a new set of Interests with the given Interest flipped done in a functional way to be
     * able to use it in a mutableState or LiveData
     *
     * @param set the set of Interests
     * @param interest the Interest to flip
     * @return the new set of Interests
     */
    fun flipInterest(set: Set<Interests>, interest: Interests): Set<Interests> {
      return if (set.contains(interest)) removeInterest(set, interest)
      else addInterest(set, interest)
    }
  }
}
