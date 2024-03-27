package com.github.se.gatherspot.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileTest {
  @Test
  fun testProfileNameConstructor() {
    val profile = Profile("John", "", "")
    assertEquals("John", profile.getUserName())
  }

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
    assertEquals("JohnDoe2", profile.getUserName())
  }

  @Test
  fun testProfileToJsonEmpty() {
    val profile = Profile("", "", "")
    val json = profile.toJson()
    assertEquals("{\"_userName\":\"\",\"_bio\":\"\",\"_image\":\"\"}", json)
  }
}
