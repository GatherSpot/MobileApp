package com.github.se.gatherspot.model

import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection

// NOTE : I will add interests once theses are pushed
/**
 * Profile data object
 *
 * @param userName the name of the user
 * @param bio the bio of the user
 * @param image link of the profile picture of the user
 * @param interests the interests of the user
 * @param id the id of the user
 */
class Profile(
    var userName: String,
    var bio: String,
    var image: String,
    override val id: String,
    var interests: Set<Interests>,
) : CollectionClass() {

  companion object {
    fun testOrganizer(): Profile {
      return Profile("John Doe", "I am not a bot", "", "TEST", setOf(Interests.FOOTBALL))
    }

    fun testParticipant(): Profile {
      return Profile("Steeve", "I play pokemon go", "", "TEST2", setOf(Interests.FOOTBALL))
    }

    fun fromFirebase(id: String, onSuccess: () -> Unit) {
      ProfileFirebaseConnection().fetch(id) { onSuccess() }
    }

    fun empty(id: String) = Profile("", "", "", id, setOf())

    /**
     * Check if a username is valid an old username can be optionally given to avoid telling we
     * can't use our own username
     *
     * @param newName the new username
     * @param oldName the old username
     * @return a string with the error message if there is one
     */
    fun checkUsername(
        newName: String,
        oldName: String?,
        res: MutableLiveData<String>,
        onSuccess: () -> Unit
    ) {
      val regex = ProfileRegex
      if (newName.isEmpty()) {
        res.postValue("Username cannot be empty")
      } else if (!regex.matches(newName)) {
        res.postValue(
            "Username can only contain letters, numbers, spaces, hyphens, and underscores")
      } else if (newName.length > 20) {
        res.postValue("Username cannot be longer than 20 characters")
      } else if (newName != oldName) {
        ProfileFirebaseConnection().ifUsernameExists(newName) {
          res.postValue(if (it) "Username already taken" else "")
          onSuccess()
        }
      }
    }

    fun add(username: String, id: String) =
        ProfileFirebaseConnection().add(Profile(username, "", "", id, Interests.new()))

    fun checkBio(bio: String): MutableLiveData<String> {
      val res = MutableLiveData<String>()
      if (bio.length > 100) {
        res.value = "Bio cannot be longer than 100 characters"
      }
      return res
    }

    val ProfileRegex = Regex("^[a-zA-Z_0-9\\-\\s]*$")
  }
}
