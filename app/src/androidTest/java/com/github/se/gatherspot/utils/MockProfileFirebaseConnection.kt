package com.github.se.gatherspot.utils

import com.github.se.gatherspot.defaults.DefaultProfiles
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
class MockProfileFirebaseConnection : ProfileFirebaseConnection() {

  var profile = DefaultProfiles.trivial
  override suspend fun fetch(id: String): Profile {
    return profile
  }

  /**
   * @return the UID of the user logged in the current instance, or null if the user is not logged
   *   in.
   */
  override fun getCurrentUserUid(): String {
    return "MC"
  }

  override fun ifUsernameExists(userName: String, onComplete: (Boolean) -> Unit) {
    onComplete(userName == "alreadyUsed")
  }

  /**
   * Fetches the profile from the database with given username if the username does not exist, the
   * function returns null if two profiles have the same username, one is returned
   *
   * @param userName the username of the user
   */
  override suspend fun fetchFromUserName(userName: String): Profile {
    return profile
  }

  override suspend fun add(element: Profile) {
    profile = element
  }

  override suspend fun update(profile: Profile) {
    this.add(profile)
  }
}
