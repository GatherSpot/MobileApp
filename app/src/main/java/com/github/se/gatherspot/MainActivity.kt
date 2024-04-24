package com.github.se.gatherspot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventRegistrationViewModel
import com.github.se.gatherspot.ui.Chat
import com.github.se.gatherspot.ui.Community
import com.github.se.gatherspot.ui.CreateEvent
import com.github.se.gatherspot.ui.EventUI
import com.github.se.gatherspot.ui.Events
import com.github.se.gatherspot.ui.LogIn
import com.github.se.gatherspot.ui.Map
import com.github.se.gatherspot.ui.Profile
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.ViewProfile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.theme.GatherSpotTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
  companion object {
    lateinit var uid: String
    lateinit var signInLauncher: ActivityResultLauncher<Intent>
  }

  private lateinit var navController: NavHostController

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    val eventsViewModel = EventsViewModel()

    signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
          this.onSignInResult(res, navController)
        }

    setContent {
      GatherSpotTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          navController = rememberNavController()
          NavHost(navController = navController, startDestination = "auth") {
            navigation(startDestination = "login", route = "auth") {
              composable("login") { LogIn(NavigationActions(navController), signInLauncher) }

              composable("signup") { SignUp(NavigationActions(navController)) }
            }

            navigation(startDestination = "events", route = "home") {
              composable("events") { Events(eventsViewModel, NavigationActions(navController)) }
              composable("event/{eventJson}") { backStackEntry ->
                val gson = Gson()
                val eventObject =
                    gson.fromJson(
                        backStackEntry.arguments?.getString("eventJson"), Event::class.java)
                EventUI(
                    event = eventObject!!,
                    navActions = NavigationActions(navController),
                    viewModel = EventRegistrationViewModel())
              }

              composable("map") { Map(NavigationActions(navController)) }

              composable("community") { Community(NavigationActions(navController)) }

              composable("chat") { Chat(NavigationActions(navController)) }

              composable("profile") {
                Profile(NavigationActions(navController))
              }
              composable("viewProfile/{uid}") { backstackEntry ->
                backstackEntry.arguments?.getString("uid")?.let {
                  ViewProfile(NavigationActions(navController), it)
                }
              }
              composable("createEvent") {
                CreateEvent(nav = NavigationActions(navController), eventUtils = EventUtils())
              }

              composable("setup") { SetUpProfile(NavigationActions(navController), uid) }
            }
          }
        }
      }
    }
  }

  private fun onSignInResult(
    result: FirebaseAuthUIAuthenticationResult,
    navController: NavHostController
  ): Int {
    if (result.resultCode == RESULT_OK) {
      if (!FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
        navController.navigate("auth")
        return RESULT_CANCELED
      } else {
        navController.navigate("home")
      }
    }
    return result.resultCode
  }
}
