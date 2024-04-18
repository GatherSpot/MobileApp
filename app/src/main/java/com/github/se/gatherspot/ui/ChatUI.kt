package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gatherspot.ProfileFirebaseConnection
import com.github.se.gatherspot.model.Profile
import com.github.se.gatherspot.model.chat.Chat
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.NavigationActions
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatUI(chat: Chat, nav: NavigationActions) {

  var profiles: List<Profile?>? = null
  var event: Event? = null
  runBlocking {
    profiles = chat.peopleIDs.map { ProfileFirebaseConnection().fetch(it) }
    event = com.github.se.gatherspot.EventFirebaseConnection().fetch(chat.eventID)
  }

  Scaffold(
      modifier = Modifier.testTag("ChatUIScreen"),
      topBar = { TopAppBar(title = { Text(event!!.title) }) },
  ) { innerPadding ->
    Text(text = "To Implement", Modifier.padding(innerPadding))
  }
}
