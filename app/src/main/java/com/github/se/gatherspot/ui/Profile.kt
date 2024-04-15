@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel
import com.github.se.gatherspot.ui.profile.ProfileView
import com.github.se.gatherspot.ui.profile.ProfileViewModel

/**
 * This function is the one that should be called when navigating to the profile screen from the
 * bottom navigation bar.
 */
@Composable
fun Profile(nav: NavigationActions, viewModel: OwnProfileViewModel) {
  // This new navController will navigate between seeing profile and editing profile
  val navController = rememberNavController()
  NavHost(navController, startDestination = "view") {
    composable("view") { ProfileView().ViewOwnProfile(nav, viewModel, navController) }
    composable("edit") { ProfileView().EditOwnProfile(nav, viewModel, navController) }
  }
}

// Those preview should show you all the functions you can call when it comes to profiles
@Preview
@Composable
fun ProfilePreview() {
  val navController = rememberNavController()
  Profile(NavigationActions(navController), OwnProfileViewModel())
}

@Preview
@Composable
fun ViewProfilePreview() {
  val profile = Profile.dummyProfile()
  ProfileView().ProfileScreen(ProfileViewModel(profile))
}
