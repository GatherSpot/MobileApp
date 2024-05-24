package com.github.se.gatherspot.model

/**
 * Enum class representing the rating of a user.
 *
 * The ratings are:
 * - UNRATED
 * - ONE_STAR
 * - TWO_STARS
 * - THREE_STARS
 * - FOUR_STARS
 * - FIVE_STARS
 */
enum class Rating {
  UNRATED,
  ONE_STAR,
  TWO_STARS,
  THREE_STARS,
  FOUR_STARS,
  FIVE_STARS;

  companion object {

    /**
     * Get the rating from a long value.
     *
     * @param rating the long value of the rating
     * @return the rating, UNRATED if the long value is not a valid rating
     */
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

    /**
     * Get the long value of a rating.
     *
     * @param rating the rating
     * @return the long value of the rating, 0 if the rating is UNRATED
     */
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
