package com.github.se.gatherspot.utils

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.model.FollowList

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
}
