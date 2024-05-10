package com.github.se.gatherspot.model

import org.junit.Test

class RatingTest {

  @Test
  fun fromInt() {
    assert(Rating.fromInt(1) == Rating.ONE_STAR)
    assert(Rating.fromInt(2) == Rating.TWO_STARS)
    assert(Rating.fromInt(3) == Rating.THREE_STARS)
    assert(Rating.fromInt(4) == Rating.FOUR_STARS)
    assert(Rating.fromInt(5) == Rating.FIVE_STARS)
    assert(Rating.fromInt(0) == Rating.UNRATED)
  }

  @Test
  fun toInt() {
    assert(Rating.toInt(Rating.ONE_STAR) == 1)
    assert(Rating.toInt(Rating.TWO_STARS) == 2)
    assert(Rating.toInt(Rating.THREE_STARS) == 3)
    assert(Rating.toInt(Rating.FOUR_STARS) == 4)
    assert(Rating.toInt(Rating.FIVE_STARS) == 5)
    assert(Rating.toInt(Rating.UNRATED) == 0)
  }
}
