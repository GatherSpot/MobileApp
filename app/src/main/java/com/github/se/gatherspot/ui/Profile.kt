@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.github.se.gatherspot.interest.Interests
import com.github.se.gatherspot.interest.Interests.Companion.newBitset
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS

@SuppressLint("UnrememberedMutableState")
@Composable
fun Profile(nav: NavigationActions) {
  Scaffold(
      topBar = {
               CenterAlignedTopAppBar(
                   title = { /*TODO*/ },
                   navigationIcon = {
                      IconButton(onClick = {
                        //onCancel()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Localized description",
                        )
                      }
                  },
                   actions = {
                       IconButton(onClick = { //onSave()
                       }) {
                           Icon(
                               imageVector = Icons.Filled.Done,
                               contentDescription = "Localized description"
                           )
                       }
                   }
               )
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        Interests.SelectEventInterests(
            selection = mutableStateOf(newBitset()),
            paddingValues = paddingValues
        )
      }
}
