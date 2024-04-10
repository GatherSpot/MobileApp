package com.github.se.gatherspot.ui.profile

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.R
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
        topBar = { TopBarWithEditButton { navController.navigate("edit") } },
        content = { paddingValues: PaddingValues ->
          ViewOwnProfileContent(viewModel)
          Log.d(ContentValues.TAG, paddingValues.toString())
        })
  }

  /**
   * This is the view that will be shown when the user is editing their own profile.
   *
   * @param nav the main nav item used for the bottom bar
   * @param viewModel the view model that holds the profile data
   * @param navController the nested navigation controller that will be used to navigate between the
   *   view and edit profile screens
   */
  @Composable
  fun EditOwnProfile(
      nav: NavigationActions,
      viewModel: OwnProfileViewModel,
      navController: NavHostController
  ) {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(
              onTabSelect = { tld -> nav.navigateTo(tld) },
              tabList = TOP_LEVEL_DESTINATIONS,
              selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
        },
        topBar = {
          val cancel = {
            viewModel.cancel()
            navController.navigate("view")
          }
          val save = {
            viewModel.save()
            navController.navigate("view")
          }
          TopBarWithSaveCancelButton(cancel, save)
        },
        content = { paddingValues: PaddingValues ->
          EditOwnProfileContent(viewModel)
          Log.d(ContentValues.TAG, paddingValues.toString())
        })
  }

  /**
   * This is the view that will be shown when the user is creating their own profile.
   *
   * @param viewModel the view model that holds the profile data
   * @param onActionDone a lambda that will be called when the user is done creating their profile,
   *   letting the parent know when to continue its task
   */
  @Composable
  fun CreateOwnProfile(viewModel: OwnProfileViewModel, onActionDone: () -> Unit) {
    Scaffold(
        topBar = {
          val save = {
            viewModel.save()
            onActionDone()
          }
          TopBarWithSaveButton(save)
        },
        content = { paddingValues: PaddingValues ->
          EditOwnProfileContent(viewModel)
          Log.d(ContentValues.TAG, paddingValues.toString())
        })
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun TopBarWithEditButton(onEditClick: () -> Unit) {
    TopAppBar(
        title = { Text("Profile") }, actions = { Button(onClick = onEditClick, modifier = Modifier.semantics { contentDescription = "edit" }
      ) { Text("Edit") } })
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun TopBarWithSaveCancelButton(onCancelClick: () -> Unit, onSaveClick: () -> Unit) {
    TopAppBar(
        title = { Text("Profile") },
        actions = {
          Button(onClick = onCancelClick, modifier = Modifier.semantics { contentDescription = "cancel" }
          ) { Text("Cancel") }
          Button(onClick = onSaveClick, modifier = Modifier.semantics { contentDescription = "save" }
          ) { Text("Save") }
        })
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  private fun TopBarWithSaveButton(onSaveClick: () -> Unit) {
    TopAppBar(
        title = { Text("Profile") }, actions = { Button(onClick = onSaveClick) { Text("Save") } })
  }

  @Composable
  private fun ShowUsernameField(username: String) {
    OutlinedTextField(
        modifier =
            Modifier.fillMaxWidth().padding(8.dp).semantics { contentDescription = "username" },
        label = { Text("username") },
        readOnly = true,
        value = username,
        onValueChange = {})
  }

  @Composable
  private fun EditUsernameField(username: String, updateUsername: (String) -> Unit) {
    OutlinedTextField(
        modifier =
            Modifier.fillMaxWidth().padding(8.dp).semantics { contentDescription = "username" },
        label = { Text("username") },
        value = username,
        onValueChange = { updateUsername(it) })
  }

  @Composable
  private fun ShowBioField(bio: String) {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio,
        readOnly = true,
        onValueChange = {},
        modifier =
            Modifier.height(150.dp).fillMaxWidth().padding(8.dp).semantics {
              contentDescription = "bio"
            })
  }

  @Composable
  private fun EditBioField(bio: String, updateBio: (String) -> Unit) {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio,
        onValueChange = { updateBio(it) },
        modifier =
            Modifier.height(150.dp).fillMaxWidth().padding(8.dp).semantics {
              contentDescription = "bio"
            })
  }

  @Composable
  private fun ShowProfileImage(imageUri: String) {
    val painter = rememberAsyncImagePainter(imageUri.ifEmpty { R.drawable.user })
    Column(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
            Image(
                painter = painter,
                contentDescription = "profile image",
                contentScale = ContentScale.Crop)
          }
        }
  }

  @Composable
  private fun EditProfileImage(imageUri: String, updateImageUri: (String) -> Unit) {
    val painter = rememberAsyncImagePainter(imageUri.ifEmpty { R.drawable.user })
    Column(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
            Image(
                painter = painter,
                contentDescription = "profile image",
                modifier = Modifier.clickable { /*select image*/},
                contentScale = ContentScale.Crop)
          }
          Text(text = "Change profile picture")
        }
  }

  @Composable
  private fun ViewOwnProfileContent(viewModel: OwnProfileViewModel) {
    // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
    // nice
    val username by viewModel.username.observeAsState(initial = "")
    val bio by viewModel.bio.observeAsState(initial = "")
    val imageUri by viewModel.image.observeAsState(initial = "")
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      ShowProfileImage(imageUri)
      ShowUsernameField(username)
      ShowBioField(bio)
    }
  }

  @Composable
  private fun EditOwnProfileContent(viewModel: OwnProfileViewModel) {
    // syntactic sugar for the view model values with sane defaults, that way the rest of code looks
    // nice
    val username by viewModel.username.observeAsState(initial = "")
    val bio by viewModel.bio.observeAsState(initial = "")
    val imageUri by viewModel.image.observeAsState(initial = "")
    val updateUsername = { s: String -> viewModel.updateUsername(s) }
    val updateBio = { s: String -> viewModel.updateBio(s) }
    val updateImageUri = { s: String -> viewModel.updateProfileImage(s) }
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      EditProfileImage(imageUri, updateImageUri)
      EditUsernameField(username, updateUsername)
      EditBioField(bio, updateBio)
    }
  }

  /**
   * This is used to show someone else's profile, and it is not editable.
   *
   * @param viewModel a view model that holds the profile to show
   */
  @Composable
  fun ProfileScreen(viewModel: ProfileViewModel) {
    val username = viewModel.username
    val bio = viewModel.bio
    val imageUri = viewModel.image

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
      ShowProfileImage(imageUri)
      ShowUsernameField(username)
      ShowBioField(bio)
    }
  }
}
