package com.github.se.gatherspot.ui.profile

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS

class ProfileView {
  /**
   * This is the view that will be shown when the user is viewing their own profile.
   *
   * @param nav the main nav item used for the bottom bar
   * @param viewModel the view model that holds the profile data
   * @param navController the nested navigation controller that will be used to navigate between the
   *   view and edit profile screens
   */
  @Composable
  fun ViewOwnProfile(
      nav: NavigationActions,
      viewModel: OwnProfileViewModel,
      navController: NavController
  ) {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(
              onTabSelect = { tld -> nav.navigateTo(tld) },
              tabList = TOP_LEVEL_DESTINATIONS,
              selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
        },
        content = { paddingValues: PaddingValues ->
          ViewOwnProfileContent(viewModel, navController)
          Log.d(ContentValues.TAG, paddingValues.toString())
        })
  }

  /**
   * This is the view that will be shown when the user is editing their own profile.
   *
   * @param nav the main nav item used for the bottom bar
   * @param viewModel the view model that holds the profile data
   */
  @Composable
  fun /**/EditOwnProfile(
      nav: NavigationActions,
      viewModel: OwnProfileViewModel,
      navController: NavController
  ) {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(
              onTabSelect = { tld -> nav.navigateTo(tld) },
              tabList = TOP_LEVEL_DESTINATIONS,
              selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
        },
        content = { paddingValues: PaddingValues ->
          EditOwnProfileContent(viewModel, navController)
          Log.d(ContentValues.TAG, paddingValues.toString())
        })
  }

  @Composable
  fun EditButton(nav: NavController) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.End) {
      // Text(text = "Edit", modifier = Modifier.clickable { edit = true })
      Icon(
          painter = painterResource(R.drawable.edit),
          contentDescription = "edit",
          modifier = Modifier.clickable { nav.navigate("edit") }.size(24.dp).testTag("edit"))
    }
  }

  @Composable
  fun SaveCancelButtons(save: () -> Unit, cancel: () -> Unit, nav: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              text = "Cancel",
              modifier =
                  Modifier.clickable {
                        cancel()
                        nav.navigate("view")
                      }
                      .testTag("cancel"))
          Text(
              text = "Save",
              modifier =
                  Modifier.clickable {
                        save()
                        nav.navigate("view")
                      }
                      .testTag("save"))
        }
  }

  @Composable
  private fun UsernameField(username: String, updateUsername: (String) -> Unit, edit: Boolean) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("usernameInput"),
        label = { Text("username") },
        value = username,
        readOnly = !edit,
        onValueChange = { updateUsername(it) })
  }

  @Composable
  fun BioField(bio: String, updateBio: (String) -> Unit, edit: Boolean) {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio,
        onValueChange = { updateBio(it) },
        readOnly = !edit,
        modifier = Modifier.height(150.dp).fillMaxWidth().padding(8.dp).testTag("bioInput"))
  }

  @Composable
  private fun ProfileImage(imageUri: String, updateImageUri: (String) -> Unit, edit: Boolean) {
    val painter = rememberAsyncImagePainter(imageUri.ifEmpty { R.drawable.user })
    Column(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
            Image(
                painter = painter,
                contentDescription = "profile image",
                modifier = Modifier.clickable { /*select image*/}.testTag("profileImage"),
                contentScale = ContentScale.Crop)
          }
          if (edit) Text(text = "Change profile picture")
        }
  }

  @Composable
  private fun ViewOwnProfileContent(viewModel: OwnProfileViewModel, navController: NavController) {
    // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
    // nice
    val username by viewModel.username.observeAsState("")
    val bio by viewModel.bio.observeAsState("")
    val imageUri by viewModel.image.observeAsState("")
    val interests = viewModel.interests.value ?: mutableSetOf()
    Column {
      EditButton(navController)
      Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
        ProfileImage(imageUri, {}, false)
        UsernameField(username, {}, false)
        BioField(bio, {}, false)
        InterestsView().ShowInterests(interests)
        Spacer(modifier = Modifier.height(56.dp))
      }
    }
  }

  @Composable
  private fun EditOwnProfileContent(viewModel: OwnProfileViewModel, navController: NavController) {
    // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
    // nice
    val username by viewModel.username.observeAsState("")
    val bio by viewModel.bio.observeAsState("")
    val imageUri by viewModel.image.observeAsState("")
    val updateUsername = { s: String -> viewModel.updateUsername(s) }
    val updateBio = { s: String -> viewModel.updateBio(s) }
    val updateImageUri = { s: String -> viewModel.updateProfileImage(s) }
    val save = { viewModel.save() }
    val cancel = { viewModel.update() }
    Column() {
      SaveCancelButtons(save, cancel, navController)
      Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(56.dp)) {
        ProfileImage(imageUri, updateImageUri, true)
        UsernameField(username, updateUsername, true)
        BioField(bio, updateBio, true)
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
    val imageUri = viewModel.image.observeAsState("").value
    val interests = viewModel.interests.observeAsState(setOf()).value

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      ProfileImage(imageUri, {}, false)
      UsernameField(username, {}, false)
      BioField(bio, {}, false)
      InterestsView().ShowInterests(interests)
      Spacer(modifier = Modifier.height(56.dp))
    }
  }
}
