package com.github.se.gatherspot.run.model

import com.github.se.gatherspot.model.Profile
import org.junit.Assert.assertThrows
import org.junit.Test

class ProfileUnitTest {
  fun setUsername() {
    val profile = Profile.testOrganizer()
    assert(profile.userName == "John Doe")
    profile.userName = "newName"
    assert(profile.userName == "newName")
  }

  @Test
  fun setUsernameEmpty() {
    val profile = Profile.testOrganizer()
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "" }
  }

  @Test
  fun setUsernameSanitization() {
    val profile = Profile.testOrganizer()
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "John@Doe#" }
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "" }
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "a".repeat(21) }
  }

  @Test
  fun setBio() {
    val profile = Profile.testOrganizer()
    assert(profile.bio == "I am not a bot")
    profile.bio = "captcha passed"
    assert(profile.bio == "captcha passed")
  }

  @Test
  fun setBioTooLong() {
    val profile = Profile.testOrganizer()
    assertThrows(IllegalArgumentException::class.java) { profile.bio = "a".repeat(101) }
  }
}
