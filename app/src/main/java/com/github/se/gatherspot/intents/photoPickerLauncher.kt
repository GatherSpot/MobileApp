package com.github.se.gatherspot.intents

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage

@Composable
fun ImagePicker(
    imageUri: State<String>,
    placeHolder: Int,
    pictureName: String,
    updateImageUri: (String) -> Unit,
    deleteImage: () -> Unit,
    shape: Shape,
    size: Dp
) {
  val photoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = {
            if (it != null) {
              updateImageUri(it.toString())
            }
          })

  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Change $pictureName picture", modifier = Modifier.padding(8.dp))
        Card(shape = shape, modifier = Modifier.padding(8.dp).size(size)) {
          AsyncImage(
              model = if (imageUri.value.isNotEmpty()) imageUri.value.toUri() else null,
              fallback = painterResource(placeHolder),
              placeholder = painterResource(placeHolder),
              contentDescription = "$pictureName image",
              modifier =
                  Modifier.clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly))
                      }
                      .testTag("profileImage"),
              contentScale = ContentScale.Crop)
        }

        if (imageUri.value.isNotEmpty()) {
          Button(onClick = { deleteImage() }) { Text(text = "Remove $pictureName picture") }
        }
      }
}

@Composable
fun ImageViewer(
    imageUri: State<String>,
    placeHolder: Int,
    pictureName: String,
    shape: Shape,
    size: Dp
) {
  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = shape, modifier = Modifier.padding(8.dp).size(size)) {
          AsyncImage(
              model = if (imageUri.value.isNotEmpty()) imageUri.value.toUri() else null,
              fallback = painterResource(placeHolder),
              placeholder = painterResource(placeHolder),
              contentDescription = "$pictureName image",
              contentScale = ContentScale.Crop)
        }
      }
}
