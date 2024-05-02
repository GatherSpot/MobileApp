package com.github.se.gatherspot.ui

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.chat.ChatsListViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.delay

@Composable
fun Chats(viewModel: ChatsListViewModel, nav: NavigationActions) {

  val state = viewModel.uiState.collectAsState()
  var previousScrollPosition by remember { mutableIntStateOf(0) }
  var loading by remember { mutableStateOf(false) }
  var fetched by remember { mutableStateOf(false) }
  var fetch by remember { mutableStateOf(false) }

  LaunchedEffect(fetch) {
    if (fetch) {
      Log.d(ContentValues.TAG, "entered")
      delay(1000)
      viewModel.fetchNext(FirebaseAuth.getInstance().currentUser?.uid)

      fetch = false
    }
  }

  Scaffold(
      topBar = {
        Column {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Add chat", fontSize = 18.sp, modifier = Modifier.testTag("AddChatText"))
                Spacer(modifier = Modifier.width(10.dp))
                Box {
                  Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = null,
                      modifier =
                          Modifier.clickable { nav.controller.navigate("createChat") }
                              .testTag("createChatMenu"))
                }
                Spacer(modifier = Modifier.width(10.dp))

                if (loading) {
                  Text("Loading next chats ... Keep scrolling")
                }
                if (fetched) {
                  Text("Fetched next chats")
                }
              }
        }
      },
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
                modifier = Modifier.fillMaxWidth().padding(paddingValues),
                contentAlignment = Alignment.TopStart) {
                  Text(
                      text = "Loading...",
                      color = Color.Black,
                      modifier = Modifier.testTag("emptyText"))
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
            //              LaunchedEffect(key1 = FirebaseAuth.getInstance().currentUser?.uid) {
            //                  try {
            //                      viewModel.fetchNext()
            //                  }
            //                  catch (e: Exception) {
            //                        Log.d("ptdr","Error fetching chats: $e")
            //                  }
            //              }
          }
        }
      }
}

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
          //          if (chatWithIndicator.chat.peopleIDs.size > 2) {
          //
          //            Text(
          //                text = "Chat between ${chatWithIndicator.chat.peopleIDs.size} people",
          //                fontWeight = FontWeight.Bold,
          //                fontSize = 10.sp)
          //          } else {
          //            Text(
          //                text =
          //                    "Chat between ${chatWithIndicator.chat.peopleIDs[0]} and
          // ${chatWithIndicator.chat.peopleIDs[1]}", // TODO: change with usernames
          //                fontWeight = FontWeight.Bold,
          //                fontSize = 10.sp)
          //          }
          Text(
              text = event.title, // TODO: change with event name
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp)
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            //            when (chatWithIndicator.unreadMessages) {
            //              0 -> Text("", color = Color(0xFF00668A), fontSize = 14.sp)
            //              else ->
            //                  Text(
            //                      "${chatWithIndicator.unreadMessages} new messages",
            //                      color = Color(255, 165, 0),
            //                      fontSize = 14.sp)
            //            }
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier.width(24.dp).height(24.dp).clickable {})
          }
        }
      }
  Divider(color = Color.Black, thickness = 1.dp)
}

@Preview
@Composable
fun ChatPreview() {
  Chats(ChatsListViewModel(), NavigationActions(rememberNavController()))
}
