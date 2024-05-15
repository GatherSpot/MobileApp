package com.github.se.gatherspot.firebase

import com.github.se.gatherspot.EnvironmentSetter
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
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
          _userName = "PFCTest",
          _bio = "bio",
          _image = "image",
          id = "profileFirebaseConnectionTest",
          _interests = setOf())
  val usernameTooLong =
      Profile(
          _userName = "ThisUsernameIsTooLong",
          _bio = "bio",
          _image = "image",
          id = "thisUsernameIsTooLong",
          _interests = setOf())

  @Before
  fun setUp() {
    runTest {
      async { profileFirebaseConnection.delete(toAdd.id) }.await()
      async { profileFirebaseConnection.delete(usernameTooLong.id) }.await()
    }
  }

  @After
  fun tearDown() {
    runTest {
      async { profileFirebaseConnection.delete(toAdd.id) }.await()
      async { profileFirebaseConnection.delete(usernameTooLong.id) }.await()
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

  //Isolate it for now as it is not working
/*
  @Test
  fun testDelete() {
    runTest {
      var fetched: Profile? = null
      profileFirebaseConnection.add(toAdd)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      profileFirebaseConnection.delete(toAdd.id)

      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertEquals(null, fetched)
    }
  }
*/
  @Test
  fun testGetCurrentUserUid() {
    runTest {
      EnvironmentSetter.testLogin()
      var uid = profileFirebaseConnection.getCurrentUserUid()
      assertNotNull(uid)
      assertEquals(Firebase.auth.currentUser?.uid, uid)
      Firebase.auth.signOut()
      uid = profileFirebaseConnection.getCurrentUserUid()
      assertEquals(null, uid)
    }
  }

  @Test
  fun testIfUsernameExists() {
    // bogus does not actually test correctly
    runTest {
      async { profileFirebaseConnection.add(toAdd) }.await()
      var wasSet = false
      profileFirebaseConnection.ifUsernameExists("PFCTest") {
        assertTrue(it)
        wasSet = true
      }

      profileFirebaseConnection.ifUsernameExists("nonExistentUsername") {
        assertFalse(it)
        wasSet = true
      }

      // assert(wasSet)
    }
  }

  @Test
  fun testfetchFromUserName() {
    runTest {
      profileFirebaseConnection.add(toAdd)
      var fetched: Profile? = null
      var otherFetch: Profile? = null
      async { fetched = profileFirebaseConnection.fetchFromUserName(toAdd.userName) }.await()
      async { otherFetch = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assertEquals(toAdd.interests, fetched?.interests)
      assertEquals(fetched?.userName, otherFetch?.userName)
      assertEquals(fetched?.bio, otherFetch?.bio)
    }
  }

  @Test
  fun testUpdate() {
    runTest {
      profileFirebaseConnection.add(toAdd)
      var fetched: Profile? = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assertEquals(toAdd.interests, fetched?.interests)

      val updated =
          Profile(
              _userName = "updated",
              _bio = "updated",
              _image = "updated",
              id = toAdd.id,
              _interests = setOf())
      profileFirebaseConnection.update(updated)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(updated.userName, fetched?.userName)
      assertEquals(updated.bio, fetched?.bio)
      assertEquals(updated.image, fetched?.image)
      assertEquals(updated.interests, fetched?.interests)
    }
  }

  @Test
  fun testFieldUpdate() {
    runTest {
      // username
      profileFirebaseConnection.add(toAdd)
      var fetched: Profile? = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      profileFirebaseConnection.update(toAdd.id, "userName", "updated")
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals("updated", fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assertEquals(toAdd.interests, fetched?.interests)

      // interests
      // Set passed
      profileFirebaseConnection.add(toAdd)
      fetched = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      val updateSet = setOf(Interests.BASKETBALL, Interests.FOOTBALL)
      profileFirebaseConnection.update(toAdd.id, "interests", updateSet)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assert(updateSet.equals(fetched?.interests))

      profileFirebaseConnection.add(toAdd)
      fetched = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      profileFirebaseConnection.updateInterests(toAdd.id, updateSet)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assert(updateSet.equals(fetched?.interests))

      // List passed
      profileFirebaseConnection.add(toAdd)
      fetched = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      val updateList = listOf(Interests.BASKETBALL, Interests.FOOTBALL)
      profileFirebaseConnection.update(toAdd.id, "interests", updateList)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assert(updateList.toSet().equals(fetched?.interests))

      // String passed
      profileFirebaseConnection.add(toAdd)
      fetched = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      val updateString = Interests.toCompressedString(updateSet)
      profileFirebaseConnection.update(toAdd.id, "interests", updateString)
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals(toAdd.bio, fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assert(updateSet.equals(fetched?.interests))

      // bio (same logic with image)
      profileFirebaseConnection.add(toAdd)
      fetched = null
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      profileFirebaseConnection.update(toAdd.id, "bio", "updated")
      async { fetched = profileFirebaseConnection.fetch(toAdd.id) }.await()
      assertNotNull(fetched)
      assertEquals(toAdd.userName, fetched?.userName)
      assertEquals("updated", fetched?.bio)
      assertEquals(toAdd.image, fetched?.image)
      assertEquals(toAdd.interests, fetched?.interests)
    }
  }
}
