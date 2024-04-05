package com.github.se.gatherspot.ui.profile

import android.content.ContentValues
import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
        EditableProfileScreen({getProfile()} ,{username,bio,imageUri -> saveProfile(username,bio,imageUri)},)
      Log.d(ContentValues.TAG, paddingValues.toString())
      }
}

//to be replaced by the real functions using firebase
private var profile: Profile? = null

//to be replaced by the real functions using firebase
fun saveProfile(userName: String, bio: String, image: String) {
    profile = Profile(userName, bio, image)
}
//To be replaced by the real function using firebase
fun getProfile(): Profile {
    if (profile == null) {
        profile = Profile("John Doe", "I am not a bot", "")
    }
    return profile as Profile
}

fun sanitizeUsername(name: String): String {
  return name.replace("[^A-Za-z0-9 _-]".toRegex(), "").take(15)
}

fun sanitizeBio(bio: String): String {
  return bio.split("\n").take(4).joinToString("\n").take(100)
}
