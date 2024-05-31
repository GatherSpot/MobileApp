package com.github.se.gatherspot.model

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.se.gatherspot.firebase.CollectionClass
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection

/**
 * Profile data object
 *
 * @param userName the name of the user
 * @param bio the bio of the user
 * @param image link of the profile picture of the user
 * @param interests the interests of the user
 * @param id the id of the user
 */
@Entity
data class Profile(
    var userName: String,
    var bio: String,
    var image: String,
    @PrimaryKey override val id: String,
    var interests: Set<Interests>,
) : CollectionClass() {
  fun withNewImage(newImage: String?): Profile {
    return Profile(userName, bio, newImage ?: image, id, interests)
  }

  companion object {
    fun testOrganizer(): Profile {
      return Profile("John Doe", "I am not a bot", "", "TEST", setOf(Interests.FOOTBALL))
    }

    fun testParticipant(): Profile {
      return Profile("Steeve", "I play pokemon go", "", "TEST2", setOf(Interests.FOOTBALL))
    }

    /**
     * Create an empty profile
     *
     * @param id the id of the user
     * @return an empty profile useful for tests and the creation of a new profile.
     */
    fun empty(id: String) = Profile("", "", "", id, setOf())

    /**
     * Check if a username is valid an old username can be optionally given to avoid telling we
     * can't use our own username
     *
     * @param newName the new username
     * @param oldName the old username
     * @return a string with the error message if there is one
     */
    suspend fun checkUsername(newName: String, oldName: String?, res: MutableLiveData<String>) {
      val regex = ProfileRegex
      if (newName.isEmpty()) {
        res.postValue("Username cannot be empty")
      } else if (!regex.matches(newName)) {
        res.postValue(
            "Username can only contain letters, numbers, spaces, hyphens, and underscores")
      } else if (newName.length > 20) {
        res.postValue("Username cannot be longer than 20 characters")
      } else if (newName != oldName) {
        if (ProfileFirebaseConnection().usernameExists(newName))
            res.postValue("Username already taken")
        else res.postValue("")
      }
    }

    /**
     * Check if a bio is valid
     *
     * @param bio the bio to check
     * @return a string with an error message if bio is invalid
     */
    fun checkBio(bio: String, res: MutableLiveData<String>) {
      if (bio.length > 100) {
        res.postValue("Bio cannot be longer than 100 characters")
      } else {
        res.postValue("")
      }
    }

    val ProfileRegex = Regex("^[a-zA-Z_0-9\\-\\s]*$")
  }
}
