package com.github.se.gatherspot.ui.topLevelDestinations

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.delay

/**
 * Composable for the chats screen.
 *
 * @param viewModel The view model for the chats view
 * @param nav The navigation actions
 */
@Composable
fun Chats(viewModel: ChatsListViewModel, nav: NavigationActions) {

  val state = viewModel.uiState.collectAsState()
  var previousScrollPosition by remember { mutableIntStateOf(0) }
  var loading by remember { mutableStateOf(false) }
  var fetched by remember { mutableStateOf(false) }
  var fetch by remember { mutableStateOf(false) }
  val isOnline by rememberUpdatedState(MainActivity.isOnline)

  LaunchedEffect(fetch) {
    if (fetch) {
      Log.d(ContentValues.TAG, "entered")
      delay(1000)
      viewModel.fetchNext(FirebaseAuth.getInstance().currentUser?.uid)

      fetch = false
    }
  }

  Scaffold(
      topBar = { ChatsTopAppBar("Chats") },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        if (fetch) {
          Text(
              modifier = Modifier.testTag("fetch").padding(vertical = 30.dp),
              text = "Fetching chats...")
        }

        val chats = state.value.list.toList()
        val lazyState = rememberLazyListState()
        when {
            chats.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isOnline) "Loading..." else "Your device is currently offline.",
                        color = Color.Black,
                        modifier = Modifier.testTag("emptyText")
                    )
                }

            fetch = true
          }
          else -> {
            LazyColumn(
                state = lazyState,
                modifier = Modifier.padding(paddingValues).testTag("chatsList")) {
                  items(chats) { chat -> ChatRow(chat, nav) }
                }

            LaunchedEffect(lazyState.isScrollInProgress) {
              loading = false
              fetched = false
              val isAtBottom = (lazyState.firstVisibleItemIndex + viewModel.PAGE_SIZE) >= chats.size
              val currentScrollPosition = lazyState.firstVisibleItemScrollOffset
              val downwards =
                  currentScrollPosition >= previousScrollPosition && currentScrollPosition > 0
              previousScrollPosition = currentScrollPosition
              if (lazyState.isScrollInProgress && isAtBottom && downwards) {
                loading = true
                viewModel.fetchNext(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                fetched = true
              }
            }
          }
        }
      }
}

/**
 * Composable for a chat row.
 *
 * @param event The event to display
 * @param navigation The navigation actions
 */
@Composable
fun ChatRow(event: Event, navigation: NavigationActions) {

  Row(
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 10.dp).clickable {
            val gson = Gson()
            val chatJson = gson.toJson(event.id)
            navigation.controller.navigate("chat/$chatJson")
            Log.e("Display", "eventJson = $chatJson")
          },
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
          Icon(
              painter = painterResource(R.drawable.chat),
              contentDescription = null,
              modifier = Modifier.size(24.dp))
        }

        Column(modifier = Modifier.weight(1f).padding(end = 1.dp)) {
          Text(text = event.title, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier.width(24.dp).height(24.dp).clickable {})
          }
        }
      }
  Divider(color = Color.Black, thickness = 1.dp)
}

/**
 * Composable for the chats top app bar.
 *
 * @param title The title of the app bar
 */
@Composable
fun ChatsTopAppBar(title: String) {
  TopAppBar(
      modifier = Modifier.testTag("chatsTopBar"),
      title = { androidx.compose.material.Text(text = title, color = Color.Black) },
      backgroundColor = Color.White,
      contentColor = Color.Black,
      elevation = 4.dp)
}

@Preview
@Composable
fun ChatPreview() {
  Chats(ChatsListViewModel(), NavigationActions(rememberNavController()))
}
