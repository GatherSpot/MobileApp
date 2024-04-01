@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.github.se.gatherspot.interest.Interests
import com.github.se.gatherspot.interest.Interests.Companion.newBitset
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gatherspot.ui.navigation.TopBarSaveCancelButton
import java.util.BitSet

@SuppressLint("UnrememberedMutableState")
@Composable
fun Profile(nav: NavigationActions) {
    val selection = mutableStateOf(newBitset())
    val og_selection : BitSet = selection.value
    val isUpToDate = mutableStateOf(true)
    isUpToDate.value = (og_selection == selection.value)
  Scaffold(
      topBar = {
               TopBarSaveCancelButton(
                   onSave = { },
                   onCancel = { },
                   title = { Text("SelectEventInterests") },
                   isUpToDate = isUpToDate
               )
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
          Interests.SelectEventInterests(
              selection = selection
          )
        }
      }
}

