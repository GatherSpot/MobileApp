package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest

class EnvironmentSetter {
  companion object {

    val profileFirebaseConnection = ProfileFirebaseConnection()
    val testLoginUID = "G6B6Z67yu7NyWhk1IzTLdnZXQEA2"
    val testLoginEmail = "neverdeleted@mail.com"

    /** This function logs in the user for testing purposes this user has his email verified */
    fun testLogin() {
      runBlocking {
        if (Firebase.auth.currentUser?.uid != testLoginUID) {
          Firebase.auth
              .signInWithEmailAndPassword(testLoginEmail, "GatherSpot,2024;") // uid =
              .await()
        }
      }
    }

    fun melvinLogin() {
      runBlocking {
        Firebase.auth
            .signInWithEmailAndPassword("melvinmalongamatouba@gmail.com", "Password12")
            .await()
      }
    }

    fun testDelete() {
      if (Firebase.auth.currentUser?.uid != null &&
          Firebase.auth.currentUser?.email != testLoginEmail) {
        runBlocking { ProfileFirebaseConnection().delete(Firebase.auth.currentUser?.uid!!) }
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

    fun allTestCleanUp(userName: String) {
      runTest { removeUserName(userName) }
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
        testDelete()
        delay(400)
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
