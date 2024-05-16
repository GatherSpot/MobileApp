package com.github.se.gatherspot.model

import com.github.se.gatherspot.R

enum class Interests {
  SPORT {
    override fun getIconId(): Int {
      return R.drawable.sports_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.sports_header
    }
  },
  FOOTBALL {
    override fun getIconId(): Int {
      return R.drawable.sports_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.foot_header
    }
  },
  BASKETBALL {
    override fun getIconId(): Int {
      return R.drawable.sports_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.basketball_header
    }
  },
  TENNIS {
    override fun getIconId(): Int {
      return R.drawable.sports_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.tennis_header
    }
  },
  BOARD_GAMES {
    override fun getIconId(): Int {
      return R.drawable.cards_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.cards_header
    }
  },
  CHESS {
    override fun getIconId(): Int {
      return R.drawable.chess_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.chess_header
    }
  },
  ROLE_PLAY {
    override fun getIconId(): Int {
      return R.drawable.theater_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.role_play_header
    }
  },
  VIDEO_GAMES {
    override fun getIconId(): Int {
      return R.drawable.gaming_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.controller_header
    }
  },
  NIGHTLIFE {
    override fun getIconId(): Int {
      return R.drawable.night_life_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.night_life_header
    }
  },
  CONCERTS {
    override fun getIconId(): Int {
      return R.drawable.concert_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.concert_header
    }
  },
  TECHNOLOGY {
    override fun getIconId(): Int {
      return R.drawable.tech_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.technology_header
    }
  },
  NETWORKING,
  SPEED_DATING {
    override fun getIconId(): Int {
      return R.drawable.dating_icon
    }
  },
  ART {
    override fun getIconId(): Int {
      return R.drawable.art_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.art_header
    }
  },
  TRAVEL {
    override fun getIconId(): Int {
      return R.drawable.travel_interest_icon
    }

    override fun getHeaderImageId(): Int {
      return R.drawable.technology_header
    }
  },
  LEISURE,
  BOWLING {
    override fun getHeaderImageId(): Int {
      return R.drawable.bowling_header
    }
  };

  open fun getIconId(): Int {
    return R.drawable.social_icon
  }

  open fun getHeaderImageId(): Int {
    return R.drawable.social_header
  }

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

fun getEventIcon(interests: Set<Interests>?): Int {
  if (interests != null && interests.isNotEmpty()) {
    val mostPreciseCategory =
        interests.toList().sortedByDescending { Interests.values().indexOf(it) }[0]
    return mostPreciseCategory.getIconId()
  } else {
    return R.drawable.social_icon
  }
}

fun getEventImageHeader(interests: Set<Interests>?): Int {
  if (interests != null && interests.isNotEmpty()) {
    val mostPreciseCategory =
        interests.toList().sortedByDescending { Interests.values().indexOf(it) }[0]
    return mostPreciseCategory.getHeaderImageId()
  } else {
    return R.drawable.social_header
  }
}
