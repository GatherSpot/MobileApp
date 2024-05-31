package com.github.se.gatherspot.ui.topLevelDestinations

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.google.gson.Gson

/**
 * Composable for the chats screen.
 *
 * @param viewModel The view model for the chats view
 * @param nav The navigation actions
 */
@Composable
fun Chats(viewModel: ChatsListViewModel, nav: NavigationActions) {

  val isOnline by rememberUpdatedState(MainActivity.isOnline)
  val state = viewModel.allEvents.observeAsState(listOf())

  Scaffold(
      modifier = Modifier.testTag("ChatsScreen"),
      topBar = { ChatsTopAppBar("Chats", viewModel) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        val lazyState = rememberLazyListState()
        fun LazyListState.isScrolledToEnd() =
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        val endOfListReached by remember { derivedStateOf { lazyState.isScrolledToEnd() } }

        val events = state.value
        when {
          events.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center) {
                  Text(
                      text =
                          if (isOnline) "No chatting group found."
                          else "Your device is currently offline.",
                      color = Color.Black,
                      modifier = Modifier.testTag("emptyText"))
                }
          }
          else -> {
            LazyColumn(
                state = lazyState,
                modifier = Modifier.padding(paddingValues).testTag("chatsList")) {
                  items(events) { event -> ChatRow(event, nav) }
                }

            LaunchedEffect(endOfListReached) { viewModel.fetchNextEvents() }
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

  Box(
      modifier =
          Modifier.clickable {
            val gson = Gson()
            val chatJson = gson.toJson(event.id)
            navigation.controller.navigate("chat/$chatJson")
            Log.e("Display", "eventJson = $chatJson")
          }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 10.dp),
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
      }
  Divider(color = Color.Black, thickness = 1.dp)
}

/**
 * Composable for the chats top app bar.
 *
 * @param title The title of the app bar
 */
@Composable
fun ChatsTopAppBar(title: String, viewModel: ChatsListViewModel) {
  val isSearchEnabled = viewModel.isSearchEnabled.observeAsState()
  var search by remember { mutableStateOf("") }

  TopAppBar(
      modifier = Modifier.testTag("chatsTopBar"),
      title = { androidx.compose.material.Text(text = title, color = Color.Black) },
      backgroundColor = Color.White,
      contentColor = Color.Black,
      elevation = 4.dp,
      actions = {
        OutlinedTextField(
            enabled = isSearchEnabled.value!!,
            value = search,
            onValueChange = {
              search = it
              viewModel.filter(search)
            },
            label = { Text("Type in event title") },
            modifier =
                Modifier.width(250.dp)
                    .testTag("searchBar")
                    .fillMaxHeight()
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp)),
        )

        IconButton(modifier = Modifier.testTag("refresh"), onClick = { viewModel.resetOffset() }) {
          Icon(
              modifier = Modifier.size(24.dp),
              painter = rememberVectorPainter(image = Icons.Filled.Refresh),
              contentDescription = "Refresh chats")
        }
      })
}

@Preview
@Composable
fun ChatPreview() {
  Chats(ChatsListViewModel(), NavigationActions(rememberNavController()))
}
