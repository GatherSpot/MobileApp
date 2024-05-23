package com.github.se.gatherspot

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.github.se.gatherspot.model.EventUtils
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.MapViewModel
import com.github.se.gatherspot.model.chat.ChatViewModel
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalTimeSerializer
import com.github.se.gatherspot.ui.ChatUI
import com.github.se.gatherspot.ui.FollowListUI
import com.github.se.gatherspot.ui.SignUp
import com.github.se.gatherspot.ui.eventUI.CreateEvent
import com.github.se.gatherspot.ui.eventUI.EditEvent
import com.github.se.gatherspot.ui.eventUI.EventUI
import com.github.se.gatherspot.ui.eventUI.EventUIViewModel
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import com.github.se.gatherspot.ui.profile.ProfileScreen
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import com.github.se.gatherspot.ui.qrcode.QRCodeScanner
import com.github.se.gatherspot.ui.theme.GatherSpotTheme
import com.github.se.gatherspot.ui.topLevelDestinations.Chats
import com.github.se.gatherspot.ui.topLevelDestinations.Events
import com.github.se.gatherspot.ui.topLevelDestinations.EventsViewModel
import com.github.se.gatherspot.ui.topLevelDestinations.LogIn
import com.github.se.gatherspot.ui.topLevelDestinations.Map
import com.github.se.gatherspot.ui.topLevelDestinations.SetUpProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.maps.android.compose.CameraPositionState
import java.time.LocalDate
import java.time.LocalTime

/**
 * The main activity for the app. This activity is responsible for setting up the navigation and
 * some global variables.
 */
class MainActivity : ComponentActivity() {
  companion object {
    lateinit var signInLauncher: ActivityResultLauncher<Intent>
    lateinit var mapLauncher: ActivityResultLauncher<String>
    var mapAccess = false
    var mapViewModel: MapViewModel? = null
    lateinit var app: Application
    var savedCameraPositionState: CameraPositionState? = null
    var selectedInterests: MutableList<Interests> = mutableListOf()
  }

  private lateinit var navController: NavHostController
  private var eventsViewModel: EventsViewModel? = null
  private var chatsViewModel: ChatsListViewModel? = null

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    // val chatViewModel = ChatViewModel()
    app = application
    mapViewModel = MapViewModel(app)

    signInLauncher =
        registerForActivityResult(
            FirebaseAuthUIActivityResultContract(),
        ) { res ->
          this.onSignInResult(res, navController)
        }
    mapLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean
          ->
          mapAccess = isGranted
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
              composable("events") {
                when {
                  eventsViewModel == null -> {
                    eventsViewModel = EventsViewModel()
                    Events(viewModel = eventsViewModel!!, nav = NavigationActions(navController))
                  }
                  else ->
                      Events(viewModel = eventsViewModel!!, nav = NavigationActions(navController))
                }
              }
              composable("event/{eventJson}") { backStackEntry ->
                // Create a new Gson instance with the custom serializers and deserializers
                val gson: Gson =
                    GsonBuilder()
                        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                        .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                        .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                        .create()
                val eventObject =
                    gson.fromJson(
                        backStackEntry.arguments?.getString("eventJson"), Event::class.java)
                EventUI(
                    event = eventObject!!,
                    navActions = NavigationActions(navController),
                    eventUIViewModel = EventUIViewModel(eventObject),
                    eventsViewModel = eventsViewModel!!)
              }
              composable("editEvent/{eventJson}") { backStackEntry ->
                val gson: Gson =
                    GsonBuilder()
                        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                        .registerTypeAdapter(LocalTime::class.java, LocalTimeSerializer())
                        .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
                        .create()

                val eventObject =
                    gson.fromJson(
                        backStackEntry.arguments?.getString("eventJson"), Event::class.java)
                EditEvent(
                    event = eventObject!!,
                    eventUtils = EventUtils(),
                    nav = NavigationActions(navController),
                    viewModel = eventsViewModel!!)
              }

              composable("map") { Map(NavigationActions(navController)) }

              composable("profile") {
                ProfileScaffold(NavigationActions(navController), viewModel<OwnProfileViewModel>())
              }

              composable("followers") {
                FollowListUI(navController, title = "Followers") {
                  FollowList.followers(FirebaseAuth.getInstance().currentUser?.uid ?: "TEST")
                }
              }
              composable("following") {
                FollowListUI(navController, title = "Following") {
                  FollowList.following(FirebaseAuth.getInstance().currentUser?.uid ?: "TEST")
                }
              }
              composable("viewProfile/{uid}") { backstackEntry ->
                backstackEntry.arguments?.getString("uid")?.let {
                  ProfileScreen(viewModel<ProfileViewModel> { ProfileViewModel(it, navController) })
                }
              }

              composable("chats") {
                when {
                  chatsViewModel == null -> {
                    chatsViewModel = ChatsListViewModel()
                    Chats(viewModel = chatsViewModel!!, nav = NavigationActions(navController))
                  }
                  else -> Chats(chatsViewModel!!, NavigationActions(navController))
                }
              }

              composable("qrCodeScanner") { QRCodeScanner(NavigationActions(navController)) }

              composable("chat/{chatJson}") { backStackEntry ->
                backStackEntry.arguments?.getString("chatJson")?.let {
                  ChatUI(
                      viewModel = ChatViewModel(it),
                      currentUserId = FirebaseAuth.getInstance().currentUser!!.uid,
                      navActions = NavigationActions(navController))
                }
              }
              composable("createEvent") {
                CreateEvent(
                    nav = NavigationActions(navController),
                    eventUtils = EventUtils(),
                    eventsViewModel!!)
              }
              composable("setup") { SetUpProfile(NavigationActions(navController)) }
            }
          }
        }
      }
    }
  }

  /**
   * Handles the result of the sign in activity.
   *
   * @param result The result of the sign in activity
   * @param navController The navigation controller
   */
  @RequiresApi(Build.VERSION_CODES.S)
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
        if (mapViewModel == null) {
          mapViewModel = MapViewModel(app)
        }
        navController.navigate("home")
      }
    }
    return result.resultCode
  }
}
