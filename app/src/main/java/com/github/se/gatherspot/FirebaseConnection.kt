package com.github.se.gatherspot

import android.content.ContentValues.TAG
import android.util.Log
import com.github.se.gatherspot.model.Category
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.UUID

class FirebaseConnection {
    companion object{

        fun getUID(): String {
            return UUID.randomUUID().toString()
        }

        fun addUser(user: User){
            val userMap : HashMap<String, Any?>
                = hashMapOf(
                    "uid" to user.uid,
                    "username" to user.username,
                    "email" to user.email,
                    "password" to user.password,
                    "profile" to user.profile.interests.toList()
                )

            val interests =
                hashMapOf(
                    "interests" to user.profile.interests.toList(),
                )

            userMap["profile"] = interests

            Firebase.firestore
                .collection("users")
                .document(user.uid)
                .set(userMap)
                .addOnSuccessListener { Log.d(TAG, "User succesfully added!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
        }

        fun updateUserInterests(uid: String, profile: Profile){
            val hm : HashMap<String, Any?> = hashMapOf(
                "profile.interests" to profile.interests.toList()
            )

            Firebase.firestore
                .collection("users")
                .document(uid)
                .update(hm)
                .addOnSuccessListener { Log.d(TAG, "Interests sucessfully added!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error for interests", e) }
        }

    }
}