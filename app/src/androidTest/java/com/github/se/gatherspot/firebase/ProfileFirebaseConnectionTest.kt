package com.github.se.gatherspot.firebase
//
// import com.github.se.gatherspot.EnvironmentSetter.Companion.testLogin
// import com.github.se.gatherspot.EnvironmentSetter.Companion.testLoginCleanUp
// import com.github.se.gatherspot.defaults.DefaultProfiles
// import com.github.se.gatherspot.model.Interests
// import com.github.se.gatherspot.model.Profile
// import com.google.firebase.Firebase
// import com.google.firebase.auth.auth
// import junit.framework.TestCase.assertEquals
// import junit.framework.TestCase.assertNotNull
// import junit.framework.TestCase.assertNull
// import kotlinx.coroutines.runBlocking
// import kotlinx.coroutines.test.runTest
// import org.junit.After
// import org.junit.Before
// import org.junit.Test
//
// class ProfileFirebaseConnectionTest {
//
//  private val profileFirebaseConnection = ProfileFirebaseConnection()
//  private val profile = DefaultProfiles.trivial
//
//  @Before fun setUp() = runBlocking { profileFirebaseConnection.delete(profile.id) }
//
//  @After fun tearDown() = runBlocking { profileFirebaseConnection.delete(profile.id) }
//
//  @Test
//  fun testAddAndFetch() {
//    runBlocking { profileFirebaseConnection.add(profile) }
//    var fetched: Profile?
//    runBlocking { fetched = profileFirebaseConnection.fetch(profile.id) }
//    assertEquals(profile, fetched)
//  }
//
//  @Test
//  fun testDelete() {
//    var fetched: Profile?
//    runBlocking {
//      profileFirebaseConnection.add(profile)
//      fetched = profileFirebaseConnection.fetch(profile.id)
//    }
//    assertNotNull(fetched)
//    runBlocking { profileFirebaseConnection.delete(profile.id) }
//    // TODO
//  }
//
//  @Test
//  fun testGetCurrentUserUid() {
//    runBlocking { testLogin() }
//    var uid = profileFirebaseConnection.getCurrentUserUid()
//    assertEquals(Firebase.auth.currentUser!!.uid, uid)
//    runBlocking { testLoginCleanUp() }
//    uid = profileFirebaseConnection.getCurrentUserUid()
//    assertNull(uid)
//  }
//
//  @Test
//  fun testIfUsernameExists() {
//    // bogus does not actually test correctly
//    //    runBlocking { profileFirebaseConnection.add(profile) }
//    //    var wasSet = false
//    //    profileFirebaseConnection.ifUsernameExists(profile.userName) {
//    //      assertTrue(it)
//    //      wasSet = true
//    //    }
//    //
//    //    profileFirebaseConnection.ifUsernameExists("nonExistentUsername") {
//    //      assertFalse(it)
//    //      wasSet = true
//    //    }
//
//    // assert(wasSet)
//  }
//
//  @Test
//  fun testFetchFromUserName() {
//    runBlocking { profileFirebaseConnection.add(profile) }
//    var fetched: Profile?
//    runBlocking { fetched = profileFirebaseConnection.fetchFromUserName(profile.userName) }
//    assertNotNull(fetched)
//    assertEquals(profile, fetched!!)
//  }
//
//  @Test
//  fun testUpdate() {
//    runBlocking { profileFirebaseConnection.add(profile) }
//    var fetched: Profile?
//    val updated = DefaultProfiles.trivialButDifferent
//    runBlocking {
//      profileFirebaseConnection.update(updated)
//      fetched = profileFirebaseConnection.fetch(profile.id)
//    }
//    assertEquals(updated, fetched)
//  }
//
//  @Test
//  fun testFieldUpdate() {
//    runTest {
//      // username
//      runBlocking { profileFirebaseConnection.add(profile) }
//      var fetched: Profile?
//      runBlocking {
//        profileFirebaseConnection.update(profile.id, "userName", "updated")
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals("updated", fetched!!.userName)
//      // interests
//      // Set passed
//      val updateSet = setOf(Interests.BASKETBALL, Interests.FOOTBALL)
//      runBlocking {
//        profileFirebaseConnection.update(profile.id, "interests", updateSet)
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals(updateSet, fetched!!.interests)
//
//      val updateSet2 = setOf(Interests.SPORT, Interests.CHESS)
//      runBlocking {
//        profileFirebaseConnection.updateInterests(profile.id, updateSet2)
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals(updateSet2, fetched!!.interests)
//
//      // List passed
//      val updateList = listOf(Interests.BASKETBALL, Interests.FOOTBALL)
//      runBlocking {
//        profileFirebaseConnection.update(profile.id, "interests", updateList)
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals(updateList.toSet(), fetched!!.interests)
//
//      // String passed
//      profileFirebaseConnection.add(profile)
//      val updateString = Interests.toCompressedString(updateSet)
//      runBlocking {
//        profileFirebaseConnection.update(profile.id, "interests", updateString)
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals(updateSet, fetched!!.interests)
//
//      // bio (same logic with image)
//      profileFirebaseConnection.add(profile)
//      fetched = null
//      runBlocking {
//        profileFirebaseConnection.update(profile.id, "bio", "updated")
//        fetched = profileFirebaseConnection.fetch(profile.id)
//      }
//      assertEquals("updated", fetched?.bio)
//    }
//  }
// }
