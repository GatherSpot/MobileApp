package com.github.se.gatherspot.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileTest {
  @Test
  fun testProfileNameSetGet() {
    val profile = Profile()
    profile.setUserName("John")
    assertEquals("John", profile.getUserName())
  }

  @Test
  fun testProfileNameSanitization() {
    val profile = Profile()
    profile.setUserName("John Doe#@!$%^&*()_+2")
    assertEquals("John Doe2", profile.getUserName())
  }

  @Test
  fun testProfileBioSetGet() {
    val profile = Profile()
    profile.setBio("I love trains")
    assertEquals("I love trains", profile.getBio())
  }

  // WARN: THE NEXT ONES WILL BREAK EVERYTIME WE ADD A NEW FIELD TO PROFILE, do not panic and simply
  // update the test if it fails
  @Test
  fun testProfileToJsonEmpty() {
    val profile = Profile("", "", "")
    val json = profile.toJson()
    assertEquals("{\"_userName\":\"\",\"_bio\":\"\",\"_image\":\"\"}", json)
  }

  @Test
  fun testProfileFromJsonEmpty() {
    val profile = Profile()
    val json = "{\"_userName\":\"\",\"_bio\":\"\",\"_image\":\"\"}"
    val newProfile = profile.fromJson(json)
    assertEquals("", newProfile.getUserName())
    assertEquals("", newProfile.getBio())
    assertEquals("", newProfile.getImage())
  }

  @Test
  fun testProfileConstructorEmpty() {
    val profile = Profile()
    assertEquals("", profile.getUserName())
    assertEquals("", profile.getBio())
    assertEquals("", profile.getImage())
  }

  @Test
  // TODO : add image when we have one to make this a trivial case
  fun testProfileConstructor() {
    val profile = Profile("John", "Doe", "")
    assertEquals("John", profile.getUserName())
    assertEquals("Doe", profile.getBio())
    assertEquals("", profile.getImage())
  }
}
