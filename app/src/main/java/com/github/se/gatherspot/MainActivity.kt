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
import com.github.se.gatherspot.ui.Chat
import com.github.se.gatherspot.ui.Community
import com.github.se.gatherspot.ui.Events
import com.github.se.gatherspot.ui.LogIn
import com.github.se.gatherspot.ui.Map
import com.github.se.gatherspot.ui.Profile
import com.github.se.gatherspot.ui.SetUpProfile
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.theme.GatherSpotTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
  companion object{
    lateinit var uid: String
  }

  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private lateinit var navController: NavHostController
  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)

    signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
          val ret = this.onSignInResult(res, navController)
          // see
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
              composable("events") { Events(NavigationActions(navController)) }

              composable("map") { Map(NavigationActions(navController)) }

              composable("community") { Community(NavigationActions(navController)) }

              composable("chat") { Chat(NavigationActions(navController)) }

              composable("profile") { Profile(NavigationActions(navController)) }

              composable("setup"){ SetUpProfile(NavigationActions(navController), uid)}
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
      }
      else {
        navController.navigate("home")
      }
    }
    return result.resultCode
  }
}
