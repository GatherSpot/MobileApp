package com.github.se.gatherspot.defaults

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile

class DefaultProfiles {
  companion object {
    val trivial = Profile("John Doe", "I am not a bot", "", "TEST", setOf(Interests.FOOTBALL))
    val trivialButDifferent =
        Profile("Jane Doe", "I am a bot", "", "TEST", setOf(Interests.BASKETBALL))

    fun withId(id: String) =
        Profile("John Doe", "I am not a bot", "", id, setOf(Interests.FOOTBALL))
  }
}
