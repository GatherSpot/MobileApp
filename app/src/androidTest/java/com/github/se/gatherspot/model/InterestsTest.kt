package com.github.se.gatherspot.model

import org.junit.Test

class InterestsTest {

  @Test
  fun addInterestsTest() {
    val interests = Interests.addInterest(Interests.new(), Interests.FOOTBALL)
    assert(interests.contains(Interests.FOOTBALL))
  }

  @Test
  fun removeInterestsTest() {
    val interests = setOf(Interests.FOOTBALL)
    assert(interests.contains(Interests.FOOTBALL))
    val newInterests = Interests.removeInterest(interests, Interests.FOOTBALL)
    assert(!newInterests.contains(Interests.FOOTBALL))
  }

  @Test
  fun switchInterestsTest() {
    val interests = setOf<Interests>()
    val newInterests = Interests.swapInterest(interests, Interests.FOOTBALL)
    assert(newInterests.contains(Interests.FOOTBALL))
    val newInterests2 = Interests.swapInterest(newInterests, Interests.FOOTBALL)
    assert(!newInterests2.contains(Interests.FOOTBALL))
  }
}
