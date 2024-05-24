package com.github.se.gatherspot.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.se.gatherspot.R
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.IdList
import com.github.se.gatherspot.model.Profile

/**
 * Composable for the follow list screen.
 *
 * @param navActions The navigation actions
 * @param title The title of the screen
 * @param ids The list of user IDs to display
 */
@Composable
fun FollowListUI(navActions: NavHostController, title: String, ids: suspend () -> IdList) {
  val fb = ProfileFirebaseConnection()
  val profiles = remember { mutableListOf<Profile>() }
  var fetched by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    val idList = ids()
    Log.d(TAG, idList.elements.toString())
    idList.elements.forEach { uid ->
      fb.fetch(uid)?.let {
        Log.d(TAG, "added")
        profiles.add(it)
      }
    }
    fetched = true
  }

  Scaffold(
      modifier = Modifier.testTag("FollowListScreen"),
      topBar = {
        TopAppBar(
            title = { Text(title, modifier = Modifier.testTag("title")) },
            backgroundColor = Color.White,
            navigationIcon = {
              IconButton(
                  onClick = { navActions.navigate("profile") },
                  modifier = Modifier.testTag("goBackToView")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to profile view")
                  }
            })
      }) { paddingValues ->
        when {
          !fetched -> Loading(title)
          else -> {
            when {
              profiles.isEmpty() -> Empty()
              else ->
                  LazyColumn(modifier = Modifier.padding(paddingValues).testTag("FollowList")) {
                    items(profiles) { profile -> UserRow(profile, navActions) }
                  }
            }
          }
        }
      }
}

/**
 * Composable for the loading screen.
 *
 * @param s The string to display
 */
@Composable
fun Loading(s: String) {
  Box(modifier = Modifier.fillMaxSize().testTag("empty"), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text("Loading your ${s.lowercase()} list")
    }
  }
}

/** Composable for the empty screen. */
@Composable
fun Empty() {
  Box(modifier = Modifier.fillMaxSize().testTag("empty"), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("No users found :(") }
  }
}

/**
 * Composable for a row containing info about a user.
 *
 * @param p The profile to display
 * @param navActions The navigation actions
 */
@Composable
fun UserRow(p: Profile, navActions: NavHostController) {
  Box(
      modifier =
          Modifier.testTag(p.userName).fillMaxSize().clickable {
            navActions.navigate("viewProfile/${p.id}")
          }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp)) {
              Column(modifier = Modifier.weight(1f)) {
                // TODO profile picture
              }

              Column(modifier = Modifier.weight(1f).padding(end = 1.dp)) {
                Text(text = p.userName, fontSize = 14.sp)
              }

              Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  androidx.compose.material3.Icon(
                      painter = painterResource(R.drawable.arrow_right),
                      contentDescription = null,
                      modifier = Modifier.width(24.dp).height(24.dp))
                }
              }
            }

        Divider(modifier = Modifier.height(1.dp))
      }
}
