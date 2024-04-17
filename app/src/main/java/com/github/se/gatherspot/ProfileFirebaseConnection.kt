package com.github.se.gatherspot

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.google.firebase.firestore.DocumentSnapshot

class ProfileFirebaseConnection : FirebaseConnectionInterface {

  override val COLLECTION = FirebaseCollection.PROFILES.toString()
  override val TAG = "ProfileFirebase"

  override fun getFromDocument(d: DocumentSnapshot): Profile? {
    if (d.getString("uid") == null) {
      return null
    }
    val uid = d.getString("uid")!!
    val name = d.getString("name")!!
    val bio = d.getString("bio")!!
    val image = d.getString("image")!!
    val interests = d.get("interests") as Set<Interests>
    return Profile(name, bio, image, uid, interests)
  }

  override fun add(profile: Profile) {}

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
