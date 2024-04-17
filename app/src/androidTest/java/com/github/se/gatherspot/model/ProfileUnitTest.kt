package com.github.se.gatherspot.model

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test

class ProfileUnitTest {
  fun setUsername() {
    val profile = Profile.dummyProfile()
    assert(profile.userName == "John Doe")
    profile.userName = "newName"
    assert(profile.userName == "newName")
  }

  @Test
  fun setUsernameEmpty() {
    val profile = Profile.dummyProfile()
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "" }
  }

  @Test
  fun setUsernameSpecialCharacters() {
    val profile = Profile.dummyProfile()
    assertThrows(IllegalArgumentException::class.java) { profile.userName = "John@Doe" }
  }

  @Test
  fun setBio() {
    val profile = Profile.dummyProfile()
    assert(profile.bio == "I am not a bot")
    profile.bio = "captcha passed"
    assert(profile.bio == "captcha passed")
  }

  @Test
  fun setBioTooLong() {
    val profile = Profile.dummyProfile()
    assertThrows(IllegalArgumentException::class.java) { profile.bio = "a".repeat(101) }
  }

  @Test
  fun firebase() {
    var profile = Profile.dummyProfile()
    profile.delete()
    profile.save("Johnny", "I am not a bot", "", setOf(Interests.BOWLING))
    profile = Profile.fromUID("TEST") {
      assert(profile.userName == "Johnny")
      assert(profile.bio == "I am not a bot")
      assert(profile.image == "")
      assert(profile.interests == setOf(Interests.BOWLING))
    }
  }

}
