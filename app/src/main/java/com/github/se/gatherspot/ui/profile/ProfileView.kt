package com.github.se.gatherspot.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.gatherspot.R
import com.github.se.gatherspot.intents.CircleImagePicker
import com.github.se.gatherspot.intents.CircleImageViewer
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gatherspot.ui.qrcode.ProfileQRCodeUI

/**
 * A composable for the profile screen.
 *
 * @param nav The navigation actions
 * @param viewModel The view model for the profile
 */
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
            ViewOwnProfileContent(viewModel, nav)
          }
        }
      })
}

/**
 * A composable for the upper bar of the profile screen.
 *
 * @param viewModel The view model for the profile
 * @param nav The navigation actions
 * @param edit The function to call when the edit button is clicked
 */
@Composable
fun TopBarOwnProfile(viewModel: OwnProfileViewModel, nav: NavigationActions, edit: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 20.dp)) {
    Followers(nav)
    Following(nav)
    Spacer(modifier = Modifier.padding(horizontal = 38.dp))
    LogOutButton(nav, viewModel)
    Spacer(modifier = Modifier.width(8.dp))
    EditButton(edit)
  }
}

/**
 * A composable for the edit button.
 *
 * @param edit The function to call when the button is clicked
 */
@Composable
fun EditButton(edit: () -> Unit) {
  Icon(
      painter = painterResource(R.drawable.edit),
      contentDescription = "edit",
      modifier = Modifier.clickable { edit() }.size(24.dp).testTag("edit"))
}

/**
 * A composable for the followers button.
 *
 * @param nav The navigation actions
 */
@Composable
fun Followers(nav: NavigationActions) {
  Column(horizontalAlignment = Alignment.Start) {
    Text(
        text = "Followers",
        modifier =
            Modifier.testTag("followersButton").clickable { nav.controller.navigate("followers") })
  }
}

/**
 * A composable for the following button.
 *
 * @param nav The navigation actions
 */
@Composable
fun Following(nav: NavigationActions) {
  Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(horizontal = 30.dp)) {
    Text(
        text = "Following",
        modifier =
            Modifier.testTag("followingButton").clickable { nav.controller.navigate("following") })
  }
}

/**
 * A composable for the save and cancel buttons.
 *
 * @param save The function to call when the save button is clicked
 * @param cancel The function to call when the cancel button is clicked
 */
@Composable
fun SaveCancelButtons(save: () -> Unit, cancel: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Cancel", modifier = Modifier.clickable { cancel() }.testTag("cancel"))
        Text(text = "Save", modifier = Modifier.clickable { save() }.testTag("save"))
      }
}

/**
 * A composable for the log out button.
 *
 * @param nav The navigation actions
 * @param viewModel The view model for the profile
 */
@Composable
fun LogOutButton(nav: NavigationActions, viewModel: OwnProfileViewModel) {
  Icon(
      Icons.AutoMirrored.Filled.ExitToApp,
      contentDescription = "logout",
      modifier = Modifier.clickable { viewModel.logout(nav) }.size(24.dp).testTag("logout"))
}

/**
 * A composable for the follow buttons.
 *
 * @param back The function to call when the back button is clicked
 * @param follow The function to call when the follow button is clicked
 * @param following Whether the user is following the profile
 * @param addFriend The function to call when the add friend button is clicked
 */
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
    username: State<String>,
    usernameValid: State<String>?,
    updateUsername: (String) -> Unit,
    edit: Boolean
) {
  Column {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("usernameInput"),
        label = { Text("username") },
        value = username.value,
        readOnly = !edit,
        onValueChange = { updateUsername(it) },
        supportingText = { Text(text = usernameValid?.value ?: "", color = Color.Red) })
  }
}

/**
 * A composable for the bio field.
 *
 * @param bio The bio
 * @param bioValid String that is empty if the bio is valid, otherwise it contains an error message
 * @param updateBio The function to call when the bio is updated
 * @param edit Whether the bio is editable
 */
@Composable
fun BioField(
    bio: State<String>,
    bioValid: State<String>?,
    updateBio: (String) -> Unit,
    edit: Boolean
) {
  Column {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio.value,
        onValueChange = { updateBio(it) },
        readOnly = !edit,
        modifier = Modifier.height(150.dp).fillMaxWidth().padding(8.dp).testTag("bioInput"),
        supportingText = { Text(text = bioValid?.value ?: "", color = Color.Red) })
  }
}

@Composable
private fun ViewOwnProfileContent(
    viewModel: OwnProfileViewModel,
    navController: NavigationActions
) {
  // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
  // nice
  val username = viewModel.username.observeAsState("")
  val bio = viewModel.bio.observeAsState("")
  val imageUrl = viewModel.image.observeAsState("")
  val interests = viewModel.interests.observeAsState()
  val uid = viewModel.uid

  Column(modifier = Modifier.testTag("ProfileScreen")) {
    TopBarOwnProfile(viewModel, navController, viewModel::edit)
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      CircleImageViewer(
          imageUri = imageUrl.value, placeHolder = R.drawable.profile, pictureName = "profile")
      UsernameField(username, null, {}, false)
      BioField(bio, null, {}, false)
      InterestsView().ShowInterests(interests.value ?: setOf())
      ProfileQRCodeUI(uid)
      Box(
          modifier = Modifier.fillMaxSize().testTag("scanQRCodeButtonContainer"),
          contentAlignment = Alignment.Center) {
            Button(
                onClick = { navController.controller.navigate("qrCodeScanner") },
                modifier = Modifier.wrapContentSize().testTag("scanQRCodeButton")) {
                  Text("Scan QR Code")
                }
          }
    }
  }
}

@Composable
private fun EditOwnProfileContent(viewModel: OwnProfileViewModel) {
  // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
  // nice
  val username = viewModel.username.observeAsState("")
  val usernameError = viewModel.userNameError.observeAsState("")
  val bio = viewModel.bio.observeAsState("")
  val bioError = viewModel.bioError.observeAsState("")
  val imageUri = viewModel.image.observeAsState("")
  val updateUsername = viewModel::updateUsername
  val updateBio = viewModel::updateBio
  val save = viewModel::save
  val cancel = viewModel::cancel
  val setImageUri = viewModel::updateProfileImage
  val deleteImage = viewModel::removeProfilePicture

  Column {
    SaveCancelButtons(save, cancel)
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(56.dp)) {
      CircleImagePicker(
          imageUri = imageUri.value,
          placeHolder = R.drawable.profile,
          pictureName = "profile",
          updateImageUri = setImageUri,
          deleteImage = deleteImage,
      )
      UsernameField(username, usernameError, updateUsername, true)
      BioField(bio, bioError, updateBio, true)
      InterestsView().EditInterests(Interests.toList(), viewModel.interests.observeAsState()) {
        viewModel.flipInterests(it)
      }
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
  val username = viewModel.username.observeAsState("")
  val bio = viewModel.bio.observeAsState("")
  val imageUri = viewModel.image.observeAsState("")
  val interests = viewModel.interests.observeAsState(setOf())
  val following = viewModel.isFollowing.observeAsState(false)
  val back = viewModel::back
  val follow = viewModel::follow
  val addFriend = viewModel::requestFriend

  Column {
    FollowButtons(back, follow, following.value, addFriend)
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      CircleImageViewer(
          imageUri = imageUri.value, placeHolder = R.drawable.profile, pictureName = "profile")
      UsernameField(username, null, {}, false)
      BioField(bio, null, {}, false)
      InterestsView().ShowInterests(interests.value)
      ProfileQRCodeUI(viewModel.target)
      Spacer(modifier = Modifier.height(56.dp))
    }
  }
}
