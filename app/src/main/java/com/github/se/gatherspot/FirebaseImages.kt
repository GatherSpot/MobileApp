package com.github.se.gatherspot

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseImages {
  private val firebaseStorage = FirebaseStorage.getInstance()
  private val PICTURE_BASE_STORAGE = firebaseStorage.getReference("images")
  private val PROFILE_PICTURE_STORAGE = "profile_images"

  /**
   * A function that pushes the profile picture to the profile image cloud storage on
   * success returns the image url, elses empty
   */
  suspend fun pushProfilePicture(imageUri: Uri, userId: String): String {
    if (imageUri != Uri.EMPTY && userId.isNotEmpty()) {
      return pushPicture(imageUri, PROFILE_PICTURE_STORAGE, userId)
    } else {
      return ""
    }
  }

  /**
   * A function that pushes the profile picture to the profile image cloud storage on
   * success returns the image url, elses empty
   */
  suspend fun pushPicture(imageUri: Uri, subFolder: String, saveAs: String): String {
    if (imageUri != Uri.EMPTY && subFolder.isNotEmpty() && saveAs.isNotEmpty()) {
      try {
        val task = PICTURE_BASE_STORAGE.child("${subFolder}/${saveAs}").putFile(imageUri).await()
        val url = task.metadata!!.reference!!.downloadUrl.await()
        return url.toString()
      } catch (e: Exception) {
        return ""
      }
    } else {
      return ""
    }
  }
}