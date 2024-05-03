package com.github.se.gatherspot.model

import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class FollowListTest {
  @Before
  fun setUp() {
    IdListFirebaseConnection().delete("TEST2", FirebaseCollection.FOLLOWERS) {}
    IdListFirebaseConnection().delete("TEST", FirebaseCollection.FOLLOWING) {}
  }

  @After
  fun tearDown() {
    IdListFirebaseConnection().delete("TEST2", FirebaseCollection.FOLLOWERS) {}
    IdListFirebaseConnection().delete("TEST", FirebaseCollection.FOLLOWING) {}
  }

  @Test
  fun testFollow() = runBlocking {
    FollowList.follow("TEST", "TEST2")
    val followers = FollowList.followers("TEST2")
    val following = FollowList.following("TEST")
    val isFollowing = FollowList.isFollowing("TEST", "TEST2")
    val isNotFollowing = FollowList.isFollowing("TEST2", "TEST")
    while (!isFollowing.isInitialized || !isNotFollowing.isInitialized) {
      {}
    }
    assert(followers.events.contains("TEST"))
    assert(following.events.contains("TEST2"))
    assert(isFollowing.value!!)
    assert(!isNotFollowing.value!!)
  }
}
