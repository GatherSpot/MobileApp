package com.github.se.gatherspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.github.se.gatherspot.ui.Chat
import com.github.se.gatherspot.ui.Community
import com.github.se.gatherspot.ui.LogIn
import com.github.se.gatherspot.ui.Events
import com.github.se.gatherspot.ui.Map
import com.github.se.gatherspot.ui.Profile
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.theme.GatherSpotTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GatherSpotTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val controller = rememberNavController()
          NavHost(navController = controller, startDestination = "auth") {

            navigation(startDestination = "login", route = "auth"){

              composable("login"){
                LogIn(NavigationActions(controller))
              }

              composable("signup"){
                SignUp(NavigationActions(controller))
              }

            }

            navigation(startDestination = "events", route = "home"){

              composable("events"){
                Events(NavigationActions(controller))
              }

              composable("map"){
                Map(NavigationActions(controller))
              }

              composable("community"){
                Community(NavigationActions(controller))
              }

              composable("chat"){
                Chat(NavigationActions(controller))
              }

              composable("profile"){
                Profile(NavigationActions(controller))
              }
            }

          }

        }
      }
    }
  }
}

