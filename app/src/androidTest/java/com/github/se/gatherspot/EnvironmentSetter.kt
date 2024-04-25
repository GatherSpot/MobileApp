package com.github.se.gatherspot

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest

class EnvironmentSetter {
  companion object {

    /** This function logs in the user for testing purposes this user has his email verified */
    fun testLogin() {

      runTest {
        val TAG = "testLogin"
        // Hard coded this is the one account easily logged in for all tests unless specified
        // otherwise
        async {
              Firebase.auth.signInWithEmailAndPassword(
                  "gatherspot2024@gmail.com", "GatherSpot,2024;")
            }
            .await()
        Log.d(TAG, "Logged in")
      }
    }
  }
}
