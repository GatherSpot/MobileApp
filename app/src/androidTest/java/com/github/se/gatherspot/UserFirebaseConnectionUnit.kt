package com.github.se.gatherspot

import com.github.se.gatherspot.model.Category
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserFirebaseConnectionUnit {
  @Test
  fun newUID() {
    val uid = UserFirebaseConnection.getUID()
    assertNotNull(uid)
    assertTrue(uid.isNotEmpty())
  }

  @Test
  fun uniqueUIDS() {
    val uid1 = UserFirebaseConnection.getUID()
    val uid2 = UserFirebaseConnection.getUID()
    val uid3 = UserFirebaseConnection.getUID()
    assertNotNull(uid1)
    assertNotNull(uid2)
    assertNotNull(uid3)
    assertNotEquals(uid1, uid2)
    assertNotEquals(uid2, uid3)
    assertNotEquals(uid1, uid3)
  }

  @Test
  fun testAddUserAndDelete() = runTest {
    val uid = UserFirebaseConnection.getUID()
    val username = "Test"
    val email = "random"
    val password = "random"
    val user = User(uid, username, email, password, Profile(emptySet()))
    UserFirebaseConnection.addUser(user)
    var userFetched: User? = null
    async { userFetched = UserFirebaseConnection.fetchUser(uid) }.await()
    assertNotNull(userFetched)
    assertEquals(uid, userFetched!!.uid)
    assertEquals(username, userFetched!!.username)
    assertEquals(password, userFetched!!.password)
    assertTrue(userFetched!!.profile.interests.isEmpty())

    UserFirebaseConnection.deleteUser(uid)
    async { userFetched = UserFirebaseConnection.fetchUser(uid) }.await()
    assertNull(userFetched)
  }

  @Test
  fun testUpdateUserInterests() = runTest {
    val uid = UserFirebaseConnection.getUID()
    val username = "Test"
    val email = "random"
    val password = "random"
    val user = User(uid, username, email, password, Profile(emptySet()))
    UserFirebaseConnection.addUser(user)
    UserFirebaseConnection.updateUserInterests(
        uid, Profile(setOf(Category.ARTS, Category.BASKETBALL)))
    var userFetched: User? = null
    async { userFetched = UserFirebaseConnection.fetchUser(uid) }.await()
    assertTrue(userFetched!!.profile.interests.contains(Category.ARTS))
    assertTrue(userFetched!!.profile.interests.contains(Category.BASKETBALL))
    UserFirebaseConnection.deleteUser(uid)
  }
}
