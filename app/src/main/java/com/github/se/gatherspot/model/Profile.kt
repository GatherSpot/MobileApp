package com.github.se.gatherspot.model

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param _userName the name of the user
 * @param _bio the bio of the user
 * @param _image link of the profile picture of the user
 */
class Profile private constructor(
    private var _userName: String,
    private var _bio: String,
    private var _image: String,
    private var _interests: Set<Interests>,
    private val uid: String
) {
    var userName: String
        get() = _userName
        set(value) {
            val regex = Regex("^[a-zA-Z_\\-\\s]*$")
            if (value.isEmpty())
                throw IllegalArgumentException("Username cannot be empty")
            if (!regex.matches(value))
                throw IllegalArgumentException("Username cannot contain special characters")
            if (value.length > 20)
                throw IllegalArgumentException("Username too long")
            _userName = value
        }
    var bio: String
        get() = _bio
        set(value) {
            if (value.length > 100)
                throw IllegalArgumentException("Bio too long")
            _bio = value
        }
    var image: String
        get() = _image
        set(value) {
            _image = value
        }
    var interests: Set<Interests>
        get() = _interests
        set(value) {
            _interests = value
        }

    fun addInterest(interest: Interests) {
        _interests = _interests.plus(interest)
    }

    fun removeInterest(interest: Interests) {
        _interests = _interests.minus(interest)
    }

    fun swapInterest(interest: Interests) {
        if (_interests.contains(interest))
            removeInterest(interest)
        else
            addInterest(interest)
    }

    fun save(userName: String, bio: String, image: String, interests: Set<Interests>) {
        this.userName = userName
        this.bio = bio
        this.image = image
        this._interests = interests
        toFirebase()
    }

    //TODO : handle situation when there is no internet connection, or it is slow
    //TODO : handle situation when there is no profile on firebase with such uid
    private val db = Firebase.firestore
    private val tag = "profiles"
    fun toFirebase() {
        val profile = hashMapOf(
            "userName" to userName,
            "bio" to bio,
            "image" to image,
            //TODO : change interests to make it more compact, maybe do it in its own class and not here
            "interests" to ""
        )
        db.collection(tag).document(uid)
            .set(profile)
            .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(tag, "Error writing document", e) }
    }

    fun fromFirebase() {
        db.collection(tag).document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(tag, "DocumentSnapshot data: ${document.data}")
                    userName = document.get("userName") as String
                    bio = document.get("bio") as String
                    image = document.get("image") as String
                    interests = setOf()
                } else {
                    Log.d(tag, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(tag, "get failed with :", exception)
            }
    }

    fun Delete() {
        db.collection(tag).document(uid).delete()
            .addOnSuccessListener { Log.d(tag, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting document", e) }
    }

    companion object {
        /**
         * Factory method to fetch a profile given a certain UID
         * @param uid the unique identifier of the user
         * @return a profile object
         */
        fun fromUID(uid: String): Profile {
            if (uid.isEmpty())
                throw IllegalArgumentException("UID cannot be empty")
            val profile = emptyProfile(uid)
            profile.fromFirebase()
            return profile
        }

        /**
         * Factory method to create an empty profile
         * useful creating a new profile on signup
         * @param uid to get from firebase when creating new account
         * @return a profile object
         */
        private fun emptyProfile(uid: String): Profile {
            return Profile("", "", "", emptySet(), uid)
        }

        /**
         * Factory method to create a dummy profile
         * useful for testing and prototyping
         * @return a profile object
         */
        fun dummyProfile(): Profile {
            return Profile("John Doe", "I am not a bot", "", setOf(Interests.FOOTBALL), "TEST_UID")
        }
    }
}