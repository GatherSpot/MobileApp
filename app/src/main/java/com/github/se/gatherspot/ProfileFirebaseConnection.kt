package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.Profile.Companion.defaultProfile
import com.github.se.gatherspot.model.User
import com.github.se.gatherspot.ui.isEmailValid
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ProfileFirebaseConnection {

  companion object {

    private const val PROFILE = "profiles"
    private const val TAG = "ProfileFirebase"

    /**
     * Returns the UID of the current profile (which is by definition the UID of the current User)
     */
    fun getUID(): String {
      return UserFirebaseConnection.getUID()
    }

    /**
     * Adds a default profile to match the user
     * @param user the user to which the profile is linked
     * @return the profile that was added
     */
    fun addDefaultProfile(user: User): Profile {
      val res = defaultProfile(user)
      addProfile(res)
      return res
    }

    /**
     * adds or edits a Profile to the database
     * @param profile the profile to be added
     *
     * /!\ This method can override the userName without checking for uniqueness of userName therefore it is made private
     */
    private fun addProfile(profile: Profile) {
      //Own non computed fields
      val bitSet = Interests.newBitset()
      profile.interests.forEach { Interests.addInterest(bitSet, it) }
      val interestsString: String = Interests.toString(bitSet)
      val profileMap: HashMap<String, Any?> =
        hashMapOf(
          "uid" to profile.uid,
          "username" to profile.userName,
          "bio" to profile.bio,
          "image" to profile.image,
          "interests" to interestsString
        )

      Firebase.firestore
        .collection(PROFILE)
        .document(profile.uid)
        .set(profileMap)
        .addOnSuccessListener { Log.d(TAG, "Profile successfully added!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error creating Profile", e) }
    }

    /**
     * Checks whether a userName is already present in Profile collection
     * @param userName the string to check
     * @return True if the userName is not found in the collection false otherwise
     */
    fun userNameIsAvailable(userName: String): Boolean {
      if (userName == "")
        return false
      if (isEmailValid(userName))
        return false
      var res = true
      Firebase.firestore
        .collection(PROFILE)
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.get("userName") == userName) {
              res = false
            }
          }
        }
      return res
    }

    /**
     * Function that allows to edit the username of the profile with uid uid
     * @param uid
     * @param userName
     *
     * if the username is not available nothing happens
     * if there is no profile with matching uid a new profile will be created
     */
    suspend fun editUserName(uid: String, userName: String) {
      if (userNameIsAvailable(userName)) {
        val profile = fetchProfile(uid)
        if (profile != null) {
          profile.userName = userName
          addProfile(profile)
        } else {
          addProfile(Profile(uid, userName, "", "", hashSetOf()))
        }
      }
    }

    /**
     * function that edits a profile fields except for userName
     * @param profile the profile with uid of document and field values of document except for username that will be ignored
     */
    suspend fun editProfile(profile: Profile) {
      val username: String = fetchProfile(profile.uid)?.userName ?: profile.userName
      profile.userName = username
      addProfile(profile)
    }

    /**
     * deletes a Profile from the database
     *
     * Due to the intimate link between profile and Users, deleteUser calls this function
     */
    fun deleteProfile(uid: String) {
      Log.d(TAG, "Deleting profile with uid: $uid")
      Firebase.firestore.collection(PROFILE).document(uid).delete().addOnFailureListener { exception
        ->
        Log.e(TAG, "Error deleting Profile", exception)
      }
    }

    /**
     * deletes the current Profile from the database
     *
     * should only be called by UserFirebaseConnection
     * Due to the intimate link between profile and Users, only one of the two Firebase Connection should add both
     */
    fun deleteCurrentProfile() {
      deleteProfile(getUID())
    }

    /**
     * fetches a profile from firestore
     *  @param uid the uid of the profile
     * @return the profile
     */
    suspend fun fetchProfile(uid: String): Profile? = suspendCancellableCoroutine { continuation ->
      Firebase.firestore
        .collection(PROFILE)
        .document(uid)
        .get()
        .addOnSuccessListener { doc ->
          val res = getProfileFromDocument(doc)
          continuation.resume(res)
        }
        .addOnFailureListener { exception ->
          Log.d(TAG, exception.toString())
          continuation.resume(null)
        }
    }

    /**
     * parses the document of a profile to construct a profile
     * @param d the document snapshot
     * @return the Profile
     */
    private fun getProfileFromDocument(d: DocumentSnapshot): Profile? {
      if (d.getString("uid") == null) {
        return null
      }
      if (d.getString("userName") == null) {
        return null
      }
      if (d.getString("userName") == "") {
        return null
      }

      val uid = d.getString("uid")!!
      val userName = d.getString("userName")!!
      val bio = d.getString("bio")!!
      val image = d.getString("image")!!
      val interests = Interests.fromString(d.getString("interests")!!)


      // val profile = map["profile"] as HashMap<*, *>
      //      val interests = profile["interests"] as List<String>

      return Profile(uid, userName, bio, image, interests)
    }


  }
}

