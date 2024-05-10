package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest

class EnvironmentSetter {
  companion object {

    val profileFirebaseConnection = ProfileFirebaseConnection()
      val testLoginUID = "CpsyL2BH9TTQKEfpDC3YwZB6NLE2"

    /** This function logs in the user for testing purposes this user has his email verified */
    fun testLogin() {
      runBlocking {
        Firebase.auth
            .signInWithEmailAndPassword("neverdeleted@mail.com", "GatherSpot,2024;")//uid =
            .await()
      }
    }

    fun testLoginCleanUp() {
      Firebase.auth.signOut()
    }

    fun testDelete() {
      if (Firebase.auth.currentUser != null) {
        Firebase.auth.currentUser!!.delete()
      }
    }

    fun signUpErrorSetUp() {
      runTest {
        // Make sure the "test" username is already in use
        async { ProfileFirebaseConnection().add(Profile("test", "", "", "t_SignUpError", setOf())) }
            .await()
      }
    }

    fun signUpSetUp(userName: String, email: String) {
      runTest {
        async {
              // Make sure the username is not in use
              var toDelete: Profile? = null
              async { toDelete = profileFirebaseConnection.fetchFromUserName(userName) }.await()
              if (toDelete != null) profileFirebaseConnection.delete(toDelete!!.id)
            }
            .await()

        // Make sure the email is not in use
        try {

          Firebase.auth.createUserWithEmailAndPassword(email, "to be Deleted 128 okay").await()
        } catch (e: FirebaseAuthUserCollisionException) {
          Log.d(
              "testSignUpSetUp",
              "User already exists you need to delete them manually from the database")
          return@runTest // If the user already exists we can't do anything from here
        } catch (e: FirebaseAuthInvalidCredentialsException) {
          Log.d("testSignUpSetUp", "Invalid email")
          return@runTest
        }

        // We just created a user with the email so now we delete him
        if (Firebase.auth.currentUser == null)
            async { Firebase.auth.signInWithEmailAndPassword(email, "to be Deleted 128 okay") }
                .await()
        async { Firebase.auth.currentUser!!.delete() }.await()
      }
    }

    fun signUpCleanUp(userName: String) {
      runTest {
        if (Firebase.auth.currentUser != null)
            async { Firebase.auth.currentUser!!.delete() }.await()
        val toDelete: Profile? = profileFirebaseConnection.fetchFromUserName(userName)
        if (toDelete != null) async { profileFirebaseConnection.delete(toDelete.id) }.await()
      }
    }

    fun allTestSetUp(userName: String, email: String) {
      runTest {
        removeUserName(userName)
        checkEmailNotUsed(email)
      }
    }

    fun allTestCleanUp(userName: String) {
      runTest {
        removeUserName(userName)
        testLoginCleanUp()
      }
    }

    private fun checkEmailNotUsed(email: String) {
      runTest {

        // Make sure the email is not in use
        try {

          Firebase.auth.createUserWithEmailAndPassword(email, "to_be_Deleted_128_okay").await()
        } catch (e: FirebaseAuthUserCollisionException) {
          Log.d(
              "testSignUpSetUp",
              "User already exists you need to delete them manually from the database")
          return@runTest // If the user already exists we can't do anything from here
        } catch (e: FirebaseAuthInvalidCredentialsException) {
          Log.d("testSignUpSetUp", "Invalid email")
          return@runTest
        }

        // We just created a user with the email so now we delete him
        if (Firebase.auth.currentUser == null)
            async { Firebase.auth.signInWithEmailAndPassword(email, "to be Deleted 128 okay") }
                .await()
        async { Firebase.auth.currentUser!!.delete() }.await()
      }
    }

    suspend private fun removeUserName(userName: String) {
      runTest {
        async {
              var toDelete: Profile? = null
              async { toDelete = profileFirebaseConnection.fetchFromUserName(userName) }.await()
              if (toDelete != null) profileFirebaseConnection.delete(toDelete!!.id)
            }
            .await()
      }
    }
  }
}
