package com.github.se.gatherspot

import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile

class ProfileFirebaseConnection {
  // used for testing purposes only
  private var _dummyProfile = Profile("John Doe", "I like trains", "", Interests.newBitset(), "")
  // used for testing purposes only
  fun getDummyProfile(): Profile {
    return _dummyProfile
  }
  // used for testing purposes only
  fun updateDummyProfile(profile: Profile) {
    _dummyProfile = profile
  }

  fun addProfile(profile: Profile) {}

  fun deleteProfile(uid: String) {}

  fun fetchProfile(uid: String): Profile {

    return Profile("John Doe", "I am not a bot", "", Interests.newBitset(), "")
  }

  fun updateProfile(profile: Profile) {}
}
