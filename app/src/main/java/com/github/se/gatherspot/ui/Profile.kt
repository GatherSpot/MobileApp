@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.se.gatherspot.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.Interests.Companion.newBitset
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.gatherspot.ui.navigation.TopBarSaveCancelButton
import java.util.BitSet

@SuppressLint("UnrememberedMutableState")
@Composable
fun Profile(nav: NavigationActions) {
    val selection by rememberSaveable { mutableStateOf(newBitset()) }
    val isUpToDate = mutableStateOf(true)
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
    Log.d(ContentValues.TAG, paddingValues.toString())
  }
          Interests.SelectEventInterests(
              selection = selection
          )
}
@Preview
@Composable
fun SelectEventInterestsPreview() {
  val selection by rememberSaveable { mutableStateOf(newBitset()) }
  Interests.SelectEventInterests(
    selection = selection
  )
}