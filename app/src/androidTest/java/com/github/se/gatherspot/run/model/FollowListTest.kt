package com.github.se.gatherspot.run.model

import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class FollowListTest {
  @Before
  fun setUp() = runBlocking {
    IdListFirebaseConnection().delete("TEST2", FirebaseCollection.FOLLOWERS)
    IdListFirebaseConnection().delete("TEST", FirebaseCollection.FOLLOWING)
  }

  @After
  fun tearDown() = runBlocking {
    IdListFirebaseConnection().delete("TEST2", FirebaseCollection.FOLLOWERS)
    IdListFirebaseConnection().delete("TEST", FirebaseCollection.FOLLOWING)
  }

  @Test
  fun testFollow() = runBlocking {
    runBlocking {}
    FollowList.follow("TEST", "TEST2")
    val followers = FollowList.followers("TEST2")
    val following = FollowList.following("TEST")
    val isFollowing = FollowList.isFollowing("TEST", "TEST2")
    val isNotFollowing = FollowList.isFollowing("TEST2", "TEST")
    assert(followers.elements.contains("TEST"))
    assert(following.elements.contains("TEST2"))
    assert(isFollowing)
    assert(!isNotFollowing)
  }
}
