package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileFirebaseConnectionTest {

  val profileFirebaseConnection = ProfileFirebaseConnection()
  var toAdd: Profile =
      Profile(
          userName = "PFCTest",
          bio = "bio",
          image = "image",
          id = "profileFirebaseConnectionTest",
          interests = setOf())

  @Before
  fun setUp() {
    runTest { profileFirebaseConnection.delete(toAdd.id) }
  }

  @After
  fun tearDown() {
    runTest {
      profileFirebaseConnection.delete(toAdd.id)
      Firebase.auth.signOut()
    }
  }

  @Test
  fun testAddAndFetch() {
    runTest {
      profileFirebaseConnection.add(toAdd)
      var fetched: Profile? = null
      fetched =
          profileFirebaseConnection.fetch(toAdd.id) {
            assertEquals(toAdd.userName, fetched?.userName)
            assertEquals(toAdd.bio, fetched?.bio)
            assertEquals(toAdd.image, fetched?.image)
            assertEquals(toAdd.interests, fetched?.interests)
          }

      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assertEquals(toAdd.interests, fetched?.interests)
    }
  }

  @Test
  fun testDelete() {
    runTest {
      var fetched: Profile? = null
      profileFirebaseConnection.add(toAdd)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      profileFirebaseConnection.delete(toAdd.id)
      delay(2000)

      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertEquals(null, fetched)
    }
  }

  @Test
  fun testIfUsernameExists() {
    // bogus does not actually test correctly
    runBlocking {
      profileFirebaseConnection.add(toAdd)
      assertTrue(profileFirebaseConnection.usernameExists(toAdd.userName))

      assertFalse(profileFirebaseConnection.usernameExists("nonExistentUsername"))

      delay(2000)
    }
  }

  @Test
  fun testFetchFromUserName() {
    runTest {
      profileFirebaseConnection.add(toAdd)
      var fetched: Profile? = null
      var otherFetch: Profile? = null
      assertEquals(toAdd, profileFirebaseConnection.fetch(toAdd.id))
      assertEquals(toAdd, profileFirebaseConnection.fetchFromUserName(toAdd.userName))
    }
  }

  @Test
  fun testUpdate() {
    runTest {
      profileFirebaseConnection.add(toAdd)
      assertEquals(toAdd, profileFirebaseConnection.fetch(toAdd.id))

      val updated =
          Profile(
              userName = "updated",
              bio = "updated",
              image = "updated",
              id = toAdd.id,
              interests = setOf())
      profileFirebaseConnection.update(updated)
      assertEquals(updated, profileFirebaseConnection.fetch(toAdd.id))
    }
  }

  @Test
  fun testFieldUpdate() {
    runTest {
      // username
      profileFirebaseConnection.add(toAdd)
      profileFirebaseConnection.update(toAdd.id, "userName", "updated")
      var fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals("updated", fetched?.userName)

      // interests
      // passing a set
      val updateSet = setOf(Interests.BASKETBALL, Interests.FOOTBALL)
      profileFirebaseConnection.update(toAdd.id, "interests", updateSet)
      fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals(updateSet, (fetched?.interests))

      // passing a list
      val updateList = listOf(Interests.CHESS, Interests.BOARD_GAMES)
      profileFirebaseConnection.update(toAdd.id, "interests", updateList)
      fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals(updateList.toSet(), fetched?.interests)

      // String passed
      val updateString = Interests.toCompressedString(updateSet)
      profileFirebaseConnection.update(toAdd.id, "interests", updateString)
      fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals(updateSet, fetched?.interests)

      // bio
      profileFirebaseConnection.update(toAdd.id, "bio", "updated")
      fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals("updated", fetched?.bio)

      // image
      profileFirebaseConnection.update(toAdd.id, "image", "updated")
      fetched = profileFirebaseConnection.fetch(toAdd.id)
      assertEquals("updated", fetched?.image)
    }
  }
}
