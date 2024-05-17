@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileScaffold
import com.github.se.gatherspot.ui.profile.ProfileScreen
import com.github.se.gatherspot.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * This function is the one that should be called when navigating to the profile screen from the
 * bottom navigation bar.
 */
@Composable
fun Profile(nav: NavigationActions) {
  // This new navController will navigate between seeing profile and editing profile
  val nestedNav = rememberNavController()
  val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
  val viewModel = ViewModelProvider(viewModelStoreOwner)[OwnProfileViewModel::class.java]
  NavHost(nestedNav, startDestination = "profile") {
    composable("profile") { ProfileScaffold(nav, NavigationActions(nestedNav), viewModel) }
    composable("followers") {
      FollowList(nav, nestedNav, title = "Followers") {
        FollowList.followers(FirebaseAuth.getInstance().currentUser?.uid ?: "TEST")
      }
    }
    composable("following") {
      FollowList(nav, nestedNav, title = "Following") {
        FollowList.following(FirebaseAuth.getInstance().currentUser?.uid ?: "TEST")
      }
    }
  }
}

/**
 * Show the profile of another user
 *
 * @param nav the navigation actions
 * @param uid the id of the user to be shown
 */
@Composable
fun ViewProfile(nav: NavigationActions, uid: String) {
  ProfileScreen(ProfileViewModel(uid, nav))
}

// Those preview should show you all the functions you can call when it comes to profiles
@Preview
@Composable
fun ProfilePreview() {
  val navController = rememberNavController()
  Profile(NavigationActions(navController))
}

@Preview
@Composable
fun ViewProfilePreview() {
  val navController = rememberNavController()
  ViewProfile(NavigationActions(navController), "TEST")
}
