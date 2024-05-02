package com.github.se.gatherspot.ui.profile

import android.app.VoiceInteractor.PickOptionRequest
import android.content.ContentValues
import android.net.Uri
import android.provider.Settings.Global
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.FirebaseImages
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
  fun EditOwnProfile(
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
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp), horizontalArrangement = Arrangement.End) {
      // Text(text = "Edit", modifier = Modifier.clickable { edit = true })
      Icon(
          painter = painterResource(R.drawable.edit),
          contentDescription = "edit",
          modifier = Modifier
              .clickable { nav.navigate("edit") }
              .size(24.dp)
              .testTag("edit"))
    }
  }

  @Composable
  fun SaveCancelButtons(save: () -> Unit, cancel: () -> Unit, nav: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              text = "Cancel",
              modifier =
              Modifier
                  .clickable {
                      cancel()
                      nav.navigate("view")
                  }
                  .testTag("cancel"))
          Text(
              text = "Save",
              modifier =
              Modifier
                  .clickable {
                      save()
                      nav.navigate("view")
                  }
                  .testTag("save"))
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Icon(
              painter = painterResource(R.drawable.backarrow),
              contentDescription = "back",
              modifier = Modifier
                  .clickable { back() }
                  .testTag("back")
                  .size(24.dp))
          Row(modifier = Modifier
              .clickable { addFriend() }
              .testTag("addFriend")) {
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
              modifier = Modifier
                  .clickable { follow() }
                  .testTag("follow"))
        }
  }

  @Composable
  private fun UsernameField(username: String, updateUsername: (String) -> Unit, edit: Boolean) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("usernameInput"),
        label = { Text("username") },
        value = username,
        readOnly = !edit,
        onValueChange = { updateUsername(it) })
  }

  @Composable
  private fun BioField(bio: String, updateBio: (String) -> Unit, edit: Boolean) {
    OutlinedTextField(
        label = { Text("Bio") },
        value = bio,
        onValueChange = { updateBio(it) },
        readOnly = !edit,
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("bioInput"))
  }

  @Composable
  private fun ProfileImage(imageUrl: String, updateImage: (Uri) -> Unit, edit: Boolean) {
      val photoPickerLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.PickVisualMedia(),
          onResult = {
              if (it != null){
                  Log.d("SELECT IMAGE : ", it.toString())
                  updateImage(it)
              }
          }
      )

      Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Card(shape = CircleShape, modifier = Modifier
              .padding(8.dp)
              .size(180.dp)) {

              AsyncImage(
                  model = imageUrl,
                  placeholder = painterResource(R.drawable.user),
                  contentDescription = "profile image",
                  modifier = Modifier
                      .clickable {
                          if(edit) {
                              photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                          }
                      }
                      .testTag("profileImage"),
                  contentScale = ContentScale.Crop
              )
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
    val imageUrl by viewModel.image.observeAsState("")
    val interests = viewModel.interests.value ?: mutableSetOf()
    Column {
      EditButton(navController)

      Column(modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(8.dp)) {
        ProfileImage(imageUrl, {}, false)
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
    val imageUrl by viewModel.image.observeAsState("")
    val updateUsername = { s: String -> viewModel.updateUsername(s) }
    val updateBio = { s: String -> viewModel.updateBio(s) }

      val uploadProfileImage: (Uri) -> Unit = {
          it -> viewModel.uploadProfileImage(it)
      }

    val save = { viewModel.save() }
    val cancel = { viewModel.update() }
    Column() {
      SaveCancelButtons(save, cancel, navController)
      Column(modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(56.dp)) {
        ProfileImage(imageUrl, uploadProfileImage, true)
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
    val following = viewModel.isFollowing.observeAsState(false).value
    val back = { viewModel.back() }
    val follow = { viewModel.follow() }
    val addFriend = { viewModel.requestFriend() }
    Column() {
      FollowButtons(back, follow, following, addFriend)
      Column(modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(8.dp)) {
        ProfileImage(imageUri, {}, false)
        UsernameField(username, {}, false)
        BioField(bio, {}, false)
        InterestsView().ShowInterests(interests)
        Spacer(modifier = Modifier.height(56.dp))
      }
    }
  }
}
