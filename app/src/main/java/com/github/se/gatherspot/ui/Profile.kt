package com.github.se.gatherspot.ui

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
  val profileData = getProfile()
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        ProfileScreen(profileData)
        Log.d(ContentValues.TAG, paddingValues.toString())
      }
}

@Composable
fun ProfileScreen(profileData: Profile) {
  val profile = getProfile()
  var username by rememberSaveable { mutableStateOf(profile.getUserName()) }
  var bio by rememberSaveable { mutableStateOf(profile.getBio()) }
  val notification = rememberSaveable { mutableStateOf("") }
  if (notification.value.isNotEmpty()) {
    Toast.makeText(LocalContext.current, notification.value, Toast.LENGTH_LONG).show()
    notification.value = ""
  }
  Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              text = "Cancel",
              modifier =
                  Modifier.clickable { notification.value = "Cancel" /*TODO: navigate back*/ })
          Text(
              text = "Save",
              modifier =
                  Modifier.clickable {
                    notification.value = "Profile Updated" /*TODO: save profile data*/
                  })
        }
    ProfileImage()

    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically) {
          Text(text = "Username", modifier = Modifier.width(105.dp), maxLines = 1)
          TextField(value = username, onValueChange = { username = it })
        }
    //    Row(modifier = Modifier
    //      .fillMaxWidth()
    //      .padding(start = 4.dp, end = 4.dp),
    //      verticalAlignment = Alignment.CenterVertically
    //    ) {
    //      Text(text = "E-mail",
    //        modifier = Modifier.width(105.dp),
    //        maxLines = 1)
    //      TextField(value = mail, onValueChange = { mail = it })
    //    }
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.Top) {
      Text(text = "Bio", modifier = Modifier.width(100.dp).padding(top = 8.dp))
      TextField(
          value = bio,
          onValueChange = { bio = it },
          maxLines = 5,
          modifier = Modifier.height(150.dp))
    }
  }
}

@Composable
fun ProfileImage() {
  val imageUri = rememberSaveable { mutableStateOf("") }
  val painter = rememberAsyncImagePainter(imageUri.value.ifEmpty { R.drawable.ic_user })

  Column(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = CircleShape, modifier = Modifier.padding(8.dp).size(180.dp)) {
          Image(
              painter = painter,
              contentDescription = "Profile Image",
              modifier = Modifier.wrapContentSize().clickable { /*TODO: select image*/},
              contentScale = ContentScale.Crop)
        }
        Text(text = "Change profile picture")
      }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
  ProfileScreen(Profile())
}

fun saveProfile(userName: String, bio: String, image: String) {
  // TODO: save profile data to database
}

fun getProfile(): Profile {
  // TODO: get profile data from database
  return Profile("John Doe", "Leafy greens enjoyer, haskell enthousiast", "")
}
