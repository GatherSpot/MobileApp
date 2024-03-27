package com.github.se.gatherspot.ui

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.gatherspot.R
import com.github.se.gatherspot.data.Profile
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS

@Composable
fun Profile(nav: NavigationActions) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        ProfileScreen()
        Log.d(ContentValues.TAG, paddingValues.toString())
      }
}

@Composable
fun ProfileScreen() {
  var edit by rememberSaveable { mutableStateOf(false) }
  val profile = getProfile()
  // Note: we need duplicate states because we need to keep the original values when the user cancels
  var username by rememberSaveable { mutableStateOf(profile.getUserName()) }
  var bio by rememberSaveable { mutableStateOf(profile.getBio()) }
  var imageUri by rememberSaveable { mutableStateOf(profile.getImage()) }
  //helper functions to update states
  
  fun cancelProfile() {
    username = profile.getUserName()
    bio = profile.getBio()
    imageUri = profile.getImage()
  }
  val toggleEdit = { edit = !edit }
  val updateUsername = { it: String -> username = sanitizeUsername(it) }
  val updateBio = { it: String -> bio = sanitizeBio(it) }
  Column(modifier = Modifier
    .verticalScroll(rememberScrollState())
    .padding(8.dp)) {
    
    Buttons(edit,toggleEdit ,{cancelProfile()},{ saveProfile(username, bio, imageUri)} )
    ProfileImage(edit,imageUri)
    UsernameField(edit, username, updateUsername)
    BioField(edit, bio, updateBio)

  }
}

@Composable
fun BioField(edit: Boolean, bio: String, updateBio: (String) -> Unit) {
    OutlinedTextField(
      label = {Text("Bio")},
      enabled = edit,
      value = bio,
      onValueChange = { updateBio (it) },
      modifier = Modifier
        .height(150.dp)
        .fillMaxWidth()
        .padding(8.dp)
    )
}

@Composable
fun UsernameField(edit: Boolean, username: String, updateUsername: (String) -> Unit) {
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      label = { Text("Username") },
      enabled = edit,
      value = username,
      onValueChange = { updateUsername(it)})
}

@Composable
fun Buttons(edit: Boolean,toggleEdit:()-> Unit, cancel: ()-> Unit, save: ()-> Unit){
  if (edit) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        text = "Cancel",
        modifier =
        Modifier.clickable {
          cancel()
          toggleEdit()
        })
      Text(
        text = "Save",
        modifier =
        Modifier.clickable {
          save()
          toggleEdit()
        })
    }
  } else {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalArrangement = Arrangement.End) {
      //Text(text = "Edit", modifier = Modifier.clickable { edit = true })
      Icon(painter = painterResource(R.drawable.edit), contentDescription = "edit", modifier = Modifier
        .clickable { toggleEdit() }
        .size(24.dp))
    }
  }
}
@Composable
fun ProfileImage(edit: Boolean, imageUri: String) {
  val painter = rememberAsyncImagePainter(imageUri.ifEmpty { R.drawable.user })
  Column(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier
          .padding(8.dp)
          .size(180.dp)) {
          Image(
              painter = painter,
              contentDescription = "Profile Image",
              modifier = Modifier

                  .clickable { /*TODO: select image*/ },
              contentScale = ContentScale.Crop
          )
        }
        if (edit) Text(text = "Change profile picture")
      }
}
@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
  ProfileScreen()
}

fun saveProfile(userName: String, bio: String, image: String) {
  // TODO: save profile data to database
}

fun getProfile(): Profile {
  // TODO: get profile data from database
  return Profile("John Doe", "Leafy greens enjoyer, haskell enthousiast", "")
}

fun sanitizeUsername(name: String): String {
  return name.replace("[^A-Za-z0-9 _-]".toRegex(), "").take(15)
}

fun sanitizeBio(bio: String): String {
    // TODO: make this better
  return bio.split("\n").take(4).joinToString("\n").take(100)
}
