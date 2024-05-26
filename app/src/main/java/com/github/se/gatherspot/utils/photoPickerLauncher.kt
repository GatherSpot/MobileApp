package com.github.se.gatherspot.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage

/**
 * A composable that allows the user to pick an image from the gallery.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 * @param updateImageUri The function to update the image URI
 * @param deleteImage The function to delete the image
 */
@Composable
fun CircleImagePicker(
    imageUri: String,
    placeHolder: Int,
    pictureName: String,
    updateImageUri: (String) -> Unit,
    deleteImage: () -> Unit
) {

  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Change $pictureName picture", modifier = Modifier.padding(8.dp))
        Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
          EditImage(imageUri, placeHolder, pictureName, updateImageUri)
        }

        if (imageUri.isNotEmpty()) {
          Button(onClick = { deleteImage() }) { Text(text = "Remove $pictureName picture") }
        }
      }
}

/**
 * A composable that displays an image in a circle.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 */
@Composable
fun CircleImageViewer(imageUri: String, placeHolder: Int, pictureName: String) {
  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
          ViewImage(imageUri, placeHolder, pictureName)
        }
      }
}

/**
 * A composable that features a banner image picker.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 * @param updateImageUri The function to update the image URI
 * @param deleteImage The function to delete the image
 */
@Composable
fun BannerImagePicker(
    imageUri: String,
    placeHolder: Int,
    pictureName: String,
    updateImageUri: (String) -> Unit,
    deleteImage: () -> Unit
) {
  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Change $pictureName picture", modifier = Modifier.padding(8.dp))
        Card(
            shape = RectangleShape,
            modifier = Modifier.padding(8.dp).fillMaxWidth().height(150.dp)) {
              EditImage(imageUri, placeHolder, pictureName, updateImageUri)
            }

        if (imageUri.isNotEmpty()) {
          Button(onClick = { deleteImage() }) { Text(text = "Remove $pictureName picture") }
        }
      }
}

/**
 * A composable that displays an image in a rectangle.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 */
@Composable
fun BannerImageViewer(imageUri: String, placeHolder: Int, pictureName: String) {
  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RectangleShape,
            modifier = Modifier.padding(8.dp).fillMaxWidth().height(150.dp)) {
              ViewImage(imageUri, placeHolder, pictureName)
            }
      }
}

/**
 * A composable that displays an image.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 */
@Composable
fun ViewImage(imageUri: String, placeHolder: Int, pictureName: String) {

  AsyncImage(
      model = if (imageUri.isNotEmpty()) imageUri.toUri() else null,
      fallback = painterResource(placeHolder),
      placeholder = painterResource(placeHolder),
      contentDescription = "$pictureName image",
      contentScale = ContentScale.Crop,
      modifier = Modifier.testTag("image"))
}

/**
 * A composable that allows the user to pick an image from the gallery.
 *
 * @param imageUri The URI of the image
 * @param placeHolder The placeholder image
 * @param pictureName The name of the picture
 * @param updateImageUri The function to update the image URI
 */
@Composable
fun EditImage(
    imageUri: String,
    placeHolder: Int,
    pictureName: String,
    updateImageUri: (String) -> Unit
) {

  val photoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = {
            if (it != null) {
              updateImageUri(it.toString())
            }
          })
  AsyncImage(
      model = if (imageUri.isNotEmpty()) imageUri.toUri() else null,
      fallback = painterResource(placeHolder),
      placeholder = painterResource(placeHolder),
      contentDescription = "$pictureName image",
      modifier =
          Modifier.clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
              }
              .testTag("image"),
      contentScale = ContentScale.Crop)
}
