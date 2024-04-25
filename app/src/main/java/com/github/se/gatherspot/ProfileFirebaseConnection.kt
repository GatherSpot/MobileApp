package com.github.se.gatherspot

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFirebaseConnection : FirebaseConnectionInterface<Profile> {

  override val COLLECTION = FirebaseCollection.PROFILES.toString()
  override val TAG = "ProfileFirebase"

  override fun getFromDocument(d: DocumentSnapshot): Profile? {
    TODO("Not yet implemented")
  }

  override fun add(element: Profile) {}

  fun deleteProfile(uid: String) {}

  fun fetchProfile(uid: String): Profile {

    return Profile()
  }

  fun updateProfile(profile: Profile) {}

  /**
   * Returns the current user's UID, or null if the user is not logged in.
   */
  fun getCurrentUserUid(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
  }
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
