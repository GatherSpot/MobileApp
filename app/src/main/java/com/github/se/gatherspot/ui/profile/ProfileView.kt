package com.github.se.gatherspot.ui.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun ProfileScaffold(nav: NavigationActions, viewModel: OwnProfileViewModel) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      },
      content = { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
          if (viewModel.isEditing.observeAsState(false).value) {
            EditOwnProfileContent(viewModel)
          } else {
            ViewOwnProfileContent(viewModel)
          }
        }
      })
}

@Composable
fun EditButton(edit: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
    // Text(text = "Edit", modifier = Modifier.clickable { edit = true })
    Icon(
        painter = painterResource(R.drawable.edit),
        contentDescription = "edit",
        modifier = Modifier.clickable { edit() }.size(24.dp).testTag("edit"))
  }
}

@Composable
fun SaveCancelButtons(save: () -> Unit, cancel: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "Cancel",
            modifier =
                Modifier.clickable {
                      cancel()
                    }
                    .testTag("cancel"))
        Text(text = "Save", modifier = Modifier.clickable { save() }.testTag("save"))
      }
}

// TODO: add state for the buttons for better ui when we have time, I want to catch up to
// propagate functionalities first
@Composable
private fun FollowButtons(
    back: () -> Unit,
    follow: () -> Unit,
    following: Boolean,
    addFriend: () -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Icon(
            painter = painterResource(R.drawable.backarrow),
            contentDescription = "back",
            modifier = Modifier.clickable { back() }.testTag("back").size(24.dp))
        Row(modifier = Modifier.clickable { addFriend() }.testTag("addFriend")) {
          Icon(
              painter = painterResource(R.drawable.add_friend),
              contentDescription = "add friend",
              modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))

          Text(text = "Add Friend")
        }
        // TODO : make if so it does not move add friend around (make if either a chip or put it
        // in a fixed size box)
        Text(
            text = if (following) "Unfollow" else "  Follow",
            modifier = Modifier.clickable { follow() }.testTag("follow"))
      }
}

@Composable
private fun UsernameField(
    username: String,
    usernameValid: String?,
    updateUsername: (String) -> Unit,
    edit: Boolean
) {
  Column {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("usernameInput"),
        label = { Text("username") },
        value = username,
        readOnly = !edit,
        onValueChange = { updateUsername(it) })
    Text(usernameValid ?: "", color = Color.Red)
  }
}

@Composable
fun BioField(bio: String, bioValid: String?, updateBio: (String) -> Unit, edit: Boolean) {
  Column() {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio,
        onValueChange = { updateBio(it) },
        readOnly = !edit,
        modifier = Modifier.height(150.dp).fillMaxWidth().padding(8.dp).testTag("bioInput"))
    Text(text = bioValid ?: "", color = Color.Red)
  }
}

@Composable
private fun ProfileImage(
    imageUrl: String,
    edit: Boolean,
    setImageEditAction: (OwnProfileViewModel.ImageEditAction) -> Unit = {},
    editAction: OwnProfileViewModel.ImageEditAction = OwnProfileViewModel.ImageEditAction.NO_ACTION,
    localImageUri: Uri = Uri.EMPTY,
    updateLocalImageUri: (Uri) -> Unit = {}
) {

  val photoPickerLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = {
            if (it != null) {
              Log.d("SELECT IMAGE : ", it.toString())
              setImageEditAction(OwnProfileViewModel.ImageEditAction.UPLOAD)
              updateLocalImageUri(it)
            }
          })

  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
          AsyncImage(
              model =
                  if (editAction == OwnProfileViewModel.ImageEditAction.NO_ACTION) {
                    imageUrl
                  } else {
                    localImageUri
                  },
              placeholder = painterResource(R.drawable.user),
              contentDescription = "profile image",
              modifier =
                  Modifier.clickable {
                        if (edit) {
                          photoPickerLauncher.launch(
                              PickVisualMediaRequest(
                                  ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                      }
                      .testTag("profileImage"),
              contentScale = ContentScale.Crop)
        }

        if (edit) Text(text = "Change profile picture")

        if (edit &&
            ((imageUrl.isNotEmpty() &&
                editAction == OwnProfileViewModel.ImageEditAction.NO_ACTION) ||
                (localImageUri != Uri.EMPTY))) {
          Button(onClick = { setImageEditAction(OwnProfileViewModel.ImageEditAction.REMOVE) }) {
            Text(text = "Remove profile picture")
          }
        }
      }
}

@Composable
private fun ViewOwnProfileContent(viewModel: OwnProfileViewModel) {
  // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
  // nice
  val username by viewModel.username.observeAsState("")
  val bio by viewModel.bio.observeAsState("")
  val imageUrl by viewModel.image.observeAsState("")
  val interests = viewModel.interests.value ?: mutableSetOf()
  Column {
    EditButton(viewModel::edit)

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      ProfileImage(imageUrl, false)
      UsernameField(username, null, {}, false)
      BioField(bio, null, {}, false)
      InterestsView().ShowInterests(interests)
      Spacer(modifier = Modifier.height(56.dp))
    }
  }
}

@Composable
private fun EditOwnProfileContent(viewModel: OwnProfileViewModel) {
  // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
  // nice
  val username = viewModel.username.observeAsState("")
  val usernameValid = viewModel.userNameValid.observeAsState()
  val bio = viewModel.bio.observeAsState("")
  val bioValid = viewModel.bioValid.observeAsState()
  val imageUrl = viewModel.image.observeAsState("")
  val updateUsername = viewModel::updateUsername
  val updateBio = viewModel::updateBio
  val save = viewModel::save
  val cancel = viewModel::cancel
  val saved = viewModel.saved.observeAsState()
  val resetSaved = viewModel::resetSaved
  val setImageEditAction = { action: OwnProfileViewModel.ImageEditAction ->
    viewModel.setImageEditAction(action)
  }
  val imageEditAction =
      viewModel.imageEditAction.observeAsState(OwnProfileViewModel.ImageEditAction.NO_ACTION)
  val localImageUriToUpload by viewModel.localImageUriToUpload.observeAsState(Uri.EMPTY)
  val setLocalImageUriToUpload = { uri: Uri -> viewModel.setLocalImageUriToUpload(uri) }
  if (saved.value == true) {
    resetSaved()
  }
  Column() {
    SaveCancelButtons(save, cancel)
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(56.dp)) {
      ProfileImage(
          imageUrl = imageUrl.value,
          edit = true,
          setImageEditAction = setImageEditAction,
          editAction = imageEditAction.value,
          localImageUri = localImageUriToUpload,
          updateLocalImageUri = setLocalImageUriToUpload)
      UsernameField(username.value, usernameValid.value, updateUsername, true)
      BioField(bio.value, bioValid.value, updateBio, true)
      InterestsView().EditInterests(Interests.toList(), viewModel.interests.observeAsState()) {
        viewModel.flipInterests(it)
      }
      Spacer(modifier = Modifier.height(56.dp))
    }
  }
}

/**
 * This is used to show someone else's profile, and it is not editable.
 *
 * @param viewModel a view model that holds the profile to show
 */
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
  val username = viewModel.username.observeAsState("").value
  val bio = viewModel.bio.observeAsState("").value
  val imageUrl = viewModel.image.observeAsState("").value
  val interests = viewModel.interests.observeAsState(setOf()).value
  val following = viewModel.isFollowing.observeAsState(false).value
  val back = viewModel::back
  val follow = viewModel::follow
  val addFriend = viewModel::requestFriend
  Column() {
    FollowButtons(back, follow, following, addFriend)
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      ProfileImage(imageUrl, false)
      UsernameField(username, null, {}, false)
      BioField(bio, null, {}, false)
      InterestsView().ShowInterests(interests)
      Spacer(modifier = Modifier.height(56.dp))
    }
  }
}
