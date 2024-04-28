package com.github.se.gatherspot.model

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection

public class FollowList {
    companion object {
        /**
         * Get the list of followers for a user
         * @param uid The user id
         */
        fun followers(uid : String): IdList {
            return IdListFirebaseConnection().fetch(uid, FirebaseCollection.FOLLOWERS){}
        }
        /**
         * Get the list of users that a user is following
         * @param uid The user id
         */
        fun following(uid : String): IdList{
            return IdListFirebaseConnection().fetch(uid, FirebaseCollection.FOLLOWING){}
        }

        /**
         * Check if user is following target
         * @param uid The user that might follow
         * @param target The user that might be followed
         * @return Boolean uid follows target
         */
        fun isFollowing(uid: String, target: String): MutableLiveData<Boolean> {
            val data = MutableLiveData<Boolean>()
            IdListFirebaseConnection().exists(uid, FirebaseCollection.FOLLOWING, target){data.value = it}
            return data
        }
        /**
         * Make user follow target
         * @param uid The user that wants to follow
         * @param target The user that is being followed
         *  uid follows target
         *  target is followed by uid
         */
        fun follow(uid : String, target : String){
           IdListFirebaseConnection().addTwoInSingleBatch(uid, FirebaseCollection.FOLLOWING, target, target, FirebaseCollection.FOLLOWERS, uid)
        }
        /**
         * Make user unfollow target
         * @param uid The user that wants to unfollow
         * @param target The user that is being unfollowed
         *  uid unfollows target
         *  target is unfollowed by uid
         */
        fun unfollow(uid : String, target : String){
            IdListFirebaseConnection().removeTwoInSingleBatch(uid, FirebaseCollection.FOLLOWING, target, target, FirebaseCollection.FOLLOWERS, uid)
        }
    }
}