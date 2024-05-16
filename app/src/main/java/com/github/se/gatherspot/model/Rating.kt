package com.github.se.gatherspot.model

enum class Rating {
  UNRATED,
  ONE_STAR,
  TWO_STARS,
  THREE_STARS,
  FOUR_STARS,
  FIVE_STARS;

  companion object {
    fun fromLong(rating: Long): Rating {
      return when (rating) {
        1L -> ONE_STAR
        2L -> TWO_STARS
        3L -> THREE_STARS
        4L -> FOUR_STARS
        5L -> FIVE_STARS
        else -> UNRATED
      }
    }

    fun toLong(rating: Rating): Long {
      return when (rating) {
        ONE_STAR -> 1
        TWO_STARS -> 2
        THREE_STARS -> 3
        FOUR_STARS -> 4
        FIVE_STARS -> 5
        else -> 0
      }
    }
  }
}
