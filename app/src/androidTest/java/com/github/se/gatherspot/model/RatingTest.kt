package com.github.se.gatherspot.model

import org.junit.Test

class RatingTest {

  @Test
  fun fromLong() {
    assert(Rating.fromLong(1L) == Rating.ONE_STAR)
    assert(Rating.fromLong(2L) == Rating.TWO_STARS)
    assert(Rating.fromLong(3L) == Rating.THREE_STARS)
    assert(Rating.fromLong(4L) == Rating.FOUR_STARS)
    assert(Rating.fromLong(5L) == Rating.FIVE_STARS)
    assert(Rating.fromLong(0) == Rating.UNRATED)
    assert(Rating.fromLong(-1) == Rating.UNRATED)
  }

  @Test
  fun toLong() {
    assert(Rating.toLong(Rating.ONE_STAR) == 1L)
    assert(Rating.toLong(Rating.TWO_STARS) == 2L)
    assert(Rating.toLong(Rating.THREE_STARS) == 3L)
    assert(Rating.toLong(Rating.FOUR_STARS) == 4L)
    assert(Rating.toLong(Rating.FIVE_STARS) == 5L)
    assert(Rating.toLong(Rating.UNRATED) == 0L)
  }
}
