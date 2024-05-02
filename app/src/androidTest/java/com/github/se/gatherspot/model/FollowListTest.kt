package com.github.se.gatherspot.model

import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.resume

class FollowListTest {
  @Before
  fun setUp() = runBlocking{
    suspendCancellableCoroutine {c ->
      IdListFirebaseConnection().delete("TEST2", FirebaseCollection.FOLLOWERS) {}
      IdListFirebaseConnection().delete("TEST", FirebaseCollection.FOLLOWING) {c.resume(Unit)}
    }
  }

  @Test
  fun testFollow() {
    FollowList.follow("TEST", "TEST2")
    val followers = FollowList.followers("TEST2")
    val following = FollowList.following("TEST")
    val isFollowing = FollowList.isFollowing("TEST", "TEST2")
    val isNotFollowing = FollowList.isFollowing("TEST2", "TEST")
    while (!followers.isInitialized ||
        !following.isInitialized ||
        !isFollowing.isInitialized ||
        !isNotFollowing.isInitialized) {
      {}
    }
    assert(followers.value!!.events.contains("TEST"))
    assert(following.value!!.events.contains("TEST2"))
    assert(isFollowing.value!!)
    assert(!isNotFollowing.value!!)
  }
}
