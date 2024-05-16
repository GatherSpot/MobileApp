@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.firebase.ProfileFirebaseConnection
import com.github.se.gatherspot.model.FollowList
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileView
import com.github.se.gatherspot.ui.profile.ProfileViewModel

/**
 * This function is the one that should be called when navigating to the profile screen from the
 * bottom navigation bar.
 */
@Composable
fun Profile(
    nav: NavigationActions,
    viewModel: OwnProfileViewModel =
        viewModel() { OwnProfileViewModel() }
) {
  // This new navController will navigate between seeing profile and editing profile
  val navController = rememberNavController()
  NavHost(navController, startDestination = "view") {
    composable("view") { ProfileView().ViewOwnProfile(nav, viewModel, navController) }
    composable("edit") { ProfileView().EditOwnProfile(nav, viewModel, navController) }
  }
}

/**
 * Show the profile of another user
 *
 * @param nav the navigation actions
 * @param uid the id of the user to be shown
 */
@Composable
fun ViewProfile(
    nav: NavigationActions,
    uid: String,
    viewModel: ProfileViewModel = viewModel {
      ProfileViewModel(uid, nav, ProfileFirebaseConnection(), FollowList())
    }
) {
  ProfileView().ProfileScreen(viewModel)
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
