package com.github.se.gatherspot.model

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection

/** Class that represents a list of followers and following users. */
class FollowList {
  companion object {
    /**
     * Get the list of followers for a user
     *
     * @param uid The user id
     */
    suspend fun followers(uid: String): IdList {
      return IdListFirebaseConnection().fetch(uid, FirebaseCollection.FOLLOWERS) {}
    }

    /**
     * Get the list of users that a user is following
     *
     * @param uid The user id
     */
    suspend fun following(uid: String): IdList {
      return IdListFirebaseConnection().fetch(uid, FirebaseCollection.FOLLOWING) {}
    }

    /**
     * Check if user is following target
     *
     * @param uid The user that might follow
     * @param targetUID The user that might be followed
     * @return Boolean uid follows target
     */
    fun isFollowing(uid: String, targetUID: String): MutableLiveData<Boolean> {
      return IdListFirebaseConnection().exists(uid, FirebaseCollection.FOLLOWING, targetUID) {}
    }

    /**
     * Make user follow target
     *
     * @param uid The user that wants to follow
     * @param targetUID The user that is being followed uid follows target target is followed by uid
     */
    fun follow(uid: String, targetUID: String) {
      IdListFirebaseConnection().addTwoInSingleBatch(
          uid,
          FirebaseCollection.FOLLOWING,
          targetUID,
          targetUID,
          FirebaseCollection.FOLLOWERS,
          uid) {}
    }

    /**
     * Make user unfollow target
     *
     * @param uid The user that wants to unfollow
     * @param targetUID The user that is being unfollowed uid unfollows target target is unfollowed
     *   by uid
     */
    fun unfollow(uid: String, targetUID: String) {
      IdListFirebaseConnection().removeTwoInSingleBatch(
          uid,
          FirebaseCollection.FOLLOWING,
          targetUID,
          targetUID,
          FirebaseCollection.FOLLOWERS,
          uid) {}
    }
  }
}
