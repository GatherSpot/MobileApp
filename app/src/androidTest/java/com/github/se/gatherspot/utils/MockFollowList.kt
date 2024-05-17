package com.github.se.gatherspot.utils

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.IdList

public class MockFollowList : FollowList() {
  private val isFollowing = MutableLiveData(false)

  override fun isFollowing(uid: String, targetUID: String): MutableLiveData<Boolean> {
    return isFollowing
  }

  override fun follow(uid: String, targetUID: String) {
    isFollowing.value = true
  }

  override fun unfollow(uid: String, targetUID: String) {
    isFollowing.value = false
  }

  override suspend fun followers(uid: String): IdList {
    return IdList("MC", listOf("1", "2", "3"), FirebaseCollection.FOLLOWERS)
  }

  /**
   * Get the list of users that a user is following
   *
   * @param uid The user id
   */
  override suspend fun following(uid: String): IdList {
    return IdList("MC", listOf("1", "2", "3"), FirebaseCollection.FOLLOWING)
  }
}
