package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.github.se.gatherspot.model.NFCService
import com.github.se.gatherspot.model.NFCStatus
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS

/**
 * NFCUI composable that displays the NFC screen. It differentiates between the organizer and the
 * participant.
 *
 * @param nav NavigationActions object that contains the navigation actions.
 * @param nfc NFCService object that contains the NFC service.
 * @param event Event object that contains the event information we want NFC to be associated with.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NFCUI(nav: NavigationActions, nfc: NFCService, event: Event) {

  var completedText by remember { mutableStateOf("") }

  LaunchedEffect(key1 = nfc.newEvent) {
    if (nfc.newEvent) {
      nfc.onEvent({ data: Profile ->
        {
          if (nfc.status == NFCStatus.ORGANIZER) {
            completedText += "\nParticipant with ID ${data.id} has scanned their NFC tag."

          } else if (nfc.status == NFCStatus.PARTICIPANT) {
            completedText = "Organizer has confirmed your attendance."
          }
        }
        // Handle NFC event
      }, event)
      nfc.newEvent = false
    }
  }

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("NFC") },
            navigationIcon = {
              IconButton(onClick = { nav.controller.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.Black)
              }
            },
        )
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      },
      modifier = Modifier.testTag("nfc")) { paddingValues ->
        if (nfc.status == NFCStatus.ORGANIZER) {
          Text(
              "Waiting for participants to scan their NFC tags.\n",
              modifier = Modifier.padding(paddingValues))
        } else if (nfc.status == NFCStatus.PARTICIPANT) {
          Text(
              "Present your phone to the organizer to confirm you came!",
              modifier = Modifier.padding(paddingValues))
        }
        Text(text = completedText, modifier = Modifier.padding(paddingValues).testTag("text"))
      }
}
