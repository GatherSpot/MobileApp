package com.github.se.gatherspot

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile

class ProfileFirebaseConnection {

  fun addProfile(profile: Profile) {}

  fun deleteProfile(uid: String) {}

  fun fetchProfile(uid: String): Profile {

    return Profile("John Doe", "I am not a bot", "", Interests.newBitset(), "")
  }

  fun updateProfile(profile: Profile) {}
}
