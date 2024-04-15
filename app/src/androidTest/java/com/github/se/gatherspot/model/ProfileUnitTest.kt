package com.github.se.gatherspot.model

import org.junit.Assert.assertThrows
import org.junit.Test

class ProfileUnitTest {
  fun setUsername(){
    val profile = Profile.dummyProfile()
    assert(profile.userName == "John Doe")
    profile.userName = "newName"
    assert(profile.userName == "newName")
  }
  @Test
  fun setUsernameEmpty(){
    val profile = Profile.dummyProfile()
    assertThrows(IllegalArgumentException::class.java) {
      profile.userName = ""
    }
  }
  @Test
  fun setUsernameSpecialCharacters(){
    val profile = Profile.dummyProfile()
    assertThrows(IllegalArgumentException::class.java) {
      profile.userName = "John@Doe"
    }
  }
  @Test
  fun addInterestsTest(){
    val profile = Profile.dummyProfile()
    profile.addInterest(Interests.BOWLING)
    assert(profile.interests.contains(Interests.BOWLING))
  }
  @Test
  fun removeInterestsTest(){
    val profile = Profile.dummyProfile()
    assert(profile.interests.contains(Interests.FOOTBALL))
    profile.removeInterest(Interests.FOOTBALL)
    assert(!profile.interests.contains(Interests.FOOTBALL))
  }
  @Test
  fun switchInterestsTest(){
    val profile = Profile.dummyProfile()
    assert(profile.interests.contains(Interests.FOOTBALL))
    profile.swapInterest(Interests.FOOTBALL)
    assert(!profile.interests.contains(Interests.FOOTBALL))
    assert(!profile.interests.contains(Interests.BOWLING))
    profile.swapInterest(Interests.BOWLING)
    assert(profile.interests.contains(Interests.BOWLING))
  }
}