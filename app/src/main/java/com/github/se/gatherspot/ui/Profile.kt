package com.github.se.gatherspot.ui

import android.content.ContentValues
import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gatherspot.ui.profile.OwnProfile
import com.github.se.gatherspot.ui.profile.OwnProfileViewModel

@Composable
fun Profile(nav: NavigationActions) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        OwnProfile(OwnProfileViewModel())
        Log.d(ContentValues.TAG, paddingValues.toString())
      }
}
