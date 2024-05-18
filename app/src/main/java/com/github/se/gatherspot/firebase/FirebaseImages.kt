package com.github.se.gatherspot.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseImages {
  private val firebaseStorage = FirebaseStorage.getInstance()
  private val PICTURE_BASE_STORAGE = firebaseStorage.getReference("images")
  private val PROFILE_PICTURE_STORAGE = "profile_images"

  /**
   * A function that pushes the profile picture to the profile image cloud storage on success
   * returns the image url, elses empty
   */
  suspend fun pushProfilePicture(imageUri: Uri, userId: String): String {
    return if (imageUri != Uri.EMPTY && userId.isNotEmpty()) {
      pushPicture(imageUri, PROFILE_PICTURE_STORAGE, userId)
    } else {
      ""
    }
  }

  /**
   * A function that pushes the profile picture to the profile image cloud storage on success
   * returns the image url, elses empty
   */
  suspend fun pushPicture(imageUri: Uri, subFolder: String, id: String): String {
    return if (imageUri != Uri.EMPTY && subFolder.isNotEmpty() && id.isNotEmpty()) {
      try {
        val task = PICTURE_BASE_STORAGE.child("${subFolder}/${id}").putFile(imageUri).await()
        val url = task.metadata!!.reference!!.downloadUrl.await()
        url.toString()
      } catch (e: Exception) {
        ""
      }
    } else {
      ""
    }
  }

  /** A function that removes the picture from the cloud storage */
  suspend fun removePicture(subFolder: String, saveAs: String): Boolean {
    return if (subFolder.isNotEmpty() && saveAs.isNotEmpty()) {
      try {
        PICTURE_BASE_STORAGE.child("${subFolder}/${saveAs}").delete().await()
        true
      } catch (e: Exception) {
        false
      }
    } else {
      false
    }
  }

  /** A function that removes the user profile picture from the cloud storage */
  suspend fun removeProfilePicture(userId: String): Boolean {
    return if (userId.isNotEmpty()) {
      removePicture(PROFILE_PICTURE_STORAGE, userId)
    } else {
      false
    }
  }
}
