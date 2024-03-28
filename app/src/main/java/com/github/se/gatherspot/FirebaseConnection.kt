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
                    "password" to user.password
                )

            val nested =
                hashMapOf(
                    "string" to user.profile.s,
                )

            userMap["profile"] = nested

            Firebase.firestore
                .collection("users")
                .document(user.uid)
                .set(userMap)
                .addOnSuccessListener { Log.d(TAG, "User succesfully added!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error creating user", e) }
        }

        fun updateUser(uid: String, interests: MutableSet<Category>){

        }

    }
}