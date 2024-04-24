package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class ProfileFirebaseConnection : FirebaseConnectionInterface<Profile> {

  override val COLLECTION = FirebaseCollection.PROFILES.toString()
  override val TAG = "ProfileFirebase"

  override fun getFromDocument(d: DocumentSnapshot): Profile? {
    TODO("Not yet implemented")
  }

  fun ifUsernameExists(username: String, onComplete: (Boolean) -> Unit) {

    var res = false
    Firebase.firestore
      .collection(COLLECTION)
      .get()
      .addOnSuccessListener { result ->
        for (document in result) {
          if (document.get("username") == username) {
            res = true
          }
          if (res) {
            break
          }
        }
        onComplete(res)
      }
      .addOnFailureListener { onComplete(true) }
  }

  override fun add(element: Profile) {}

  fun deleteProfile(uid: String) {}

  fun fetchProfile(uid: String): Profile {

    return Profile()
  }

  fun updateProfile(profile: Profile) {}
  // THE NEXT THREE ARE USED FOR TESTS
  private lateinit var dummyProfile: Profile

  fun dummySave(profile: Profile) {
    dummyProfile = profile
  }

  fun dummyFetch(): Profile {
    if (!::dummyProfile.isInitialized) {
      dummyProfile =
          Profile("John Doe", "I am not a bot", "", "", setOf(Interests.BOWLING, Interests.CHESS))
    }
    return dummyProfile
  }
}
