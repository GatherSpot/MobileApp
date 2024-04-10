package com.github.se.gatherspot

import android.util.Log
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class ProfileFirebaseConnection {

  companion object {

    const val PROFILE = "profiles"
    const val TAG = "ProfileFirebase"

    /**
     * Returns the UID of the current profile which is also the UID of the current User
     */
    fun getUID(): String {
      return UserFirebaseConnection.getUID()
    }

    /**
     * adds a Profile to the database
     * @param profile the profile to be added
     *
     * /!\ Due to the intimate link between profile and Users, only one of the two Firebase Connection should add both
     */
    fun addProfile(profile: Profile) {
      //Own non computed fields
      val bitSet = Interests.newBitset()
      profile.interests.forEach{Interests.addInterest(bitSet, it)}
      val interestsString : String = Interests.toString(bitSet)
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
     * deletes a Profile from the database
     *
     * /!\ Due to the intimate link between profile and Users, only one of the two Firebase Connection should add both
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
     * /!\ Due to the intimate link between profile and Users, only one of the two Firebase Connection should add both
     */
    fun deleteCurrentProfile() {
      deleteProfile(getUID())
    }

    /**
     *
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

    private fun getProfileFromDocument(d: DocumentSnapshot): Profile? {
      if (d.getString("uid") == null) {
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

    /**
     *
     *
     * /!!!!!!\ This implementation asssumes that the User userName and the Profile userName are the same
     * Completely false otherwise
     */
    fun usernameExists(userName: String, onComplete: (Boolean) -> Unit) {

      UserFirebaseConnection.usernameExists(userName, onComplete)
    }


  }





  /*
  Unnecessary as the addProfile is simultaneously an update method
  This is a symbolic distinction to reflect the fact that the field shared between profile and User should not be changed frivilously
  It checks that the shared fields are the same before calling addProfile
  If not (At the moment, check comments) it does NOT go through
   */
  suspend fun updateProfile(profile: Profile) {
    val user : User? = UserFirebaseConnection.fetchUser(profile.uid)
    if (user == null){ // user does not exist, somthing went really wrong
      Log.e(TAG, "Error updating the Profile with uid: "+ profile.uid + " profile has no user")
      return
    }
    if (profile.uid == user.uid && profile.userName == user.username){
      addProfile(profile)

    } else {


      // Update both OR reject the update altogether

      //Update both
      //Update shared field to match that of profile

      /*
      UserFirebaseConnection.addUser(User(profile.uid, profile.userName, user.email, user.password))
      addProfile(profile)
      */


      //Update shared field to match that of User
      /*
      addProfile (Profile(user.uid, user.username, profile.bio, profile.image, profile.interests))
      */


      //Reject
      Log.e(TAG, "uid and userName should not be changed here, Update rejected")

    }


  }



}


