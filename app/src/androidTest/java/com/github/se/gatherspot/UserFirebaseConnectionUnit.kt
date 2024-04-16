package com.github.se.gatherspot

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

  val UserFirebaseConnection = UserFirebaseConnection()

  @Test
  fun newUID() {
    val uid = UserFirebaseConnection.getID()
    assertNotNull(uid)
    assertTrue(uid.isNotEmpty())
  }

  @Test
  fun uniqueUIDS() {
    val uid1 = UserFirebaseConnection.getID()
    val uid2 = UserFirebaseConnection.getID()
    val uid3 = UserFirebaseConnection.getID()
    assertNotNull(uid1)
    assertNotNull(uid2)
    assertNotNull(uid3)
    assertNotEquals(uid1, uid2)
    assertNotEquals(uid2, uid3)
    assertNotEquals(uid1, uid3)
  }

  @Test
  fun testaddAndDelete() = runTest {
    val uid = UserFirebaseConnection.getID()
    val username = "Test"
    val email = "random"
    val password = "random"
    val user = User(uid, username, email, password)
    UserFirebaseConnection.add(user)
    var userFetched: User? = null
    async { userFetched = UserFirebaseConnection.fetch(uid) as User? }.await()
    assertNotNull(userFetched)
    assertEquals(uid, userFetched!!.id)
    assertEquals(username, userFetched!!.username)
    assertEquals(password, userFetched!!.password)

    UserFirebaseConnection.delete(uid)
    async { userFetched = UserFirebaseConnection.fetch(uid) as User? }.await()
    assertNull(userFetched)
  }
}
