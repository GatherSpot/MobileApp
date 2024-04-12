package com.github.se.gatherspot.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp as dp
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.EventFirebaseConnection
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.gson.Gson
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

/** Composable that displays events * */
@Composable
fun Events(viewModel: EventsViewModel, nav: NavigationActions) {
  val state = viewModel.uiState.collectAsState()
  var previousScrollPosition by remember { mutableIntStateOf(0) }
  var loading by remember { mutableStateOf(false) }
  var fetched by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.testTag("EventsScreen"),
      topBar = {
        Column {
          Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Filter events", fontSize = 18.sp, modifier = Modifier.testTag("filter"))
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null,
                    modifier = Modifier.clickable {}.testTag("filterMenu"))
                Spacer(modifier = Modifier.width(80.dp))
                Text(
                    text = "Create an event",
                    fontSize = 18.sp,
                    modifier = Modifier.testTag("create"))
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier =
                        Modifier.clickable { nav.controller.navigate("createEvent") }
                            .testTag("createMenu"))
              }

          if (loading) {
            Text("Loading new events ... Keep scrolling")
          }
          if (fetched) {
            Text("Fetched new events")
          }
        }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        val events = state.value.list
        val lazyState = rememberLazyListState()
        when {
          events.isEmpty() -> Empty()
          else -> {
            LazyColumn(
                state = lazyState,
                modifier = Modifier.padding(paddingValues).testTag("eventsList")) {
                  items(events) { event -> EventRow(event, nav) }
                }

            LaunchedEffect(lazyState.isScrollInProgress) {
              loading = false
              fetched = false
              val isAtBottom = (lazyState.firstVisibleItemIndex + viewModel.PAGESIZE) >= events.size
              val currentScrollPosition = lazyState.firstVisibleItemScrollOffset
              val downwards =
                  currentScrollPosition >= previousScrollPosition && currentScrollPosition > 0
              previousScrollPosition = currentScrollPosition
              if (lazyState.isScrollInProgress && isAtBottom && downwards) {
                loading = true
                delay(1000)
                viewModel.fetchNext()
                fetched = true
              }
            }
          }
        }
      }
}

@Composable
fun Empty() {
  Row(modifier = Modifier.padding(vertical = 40.dp).testTag("empty")) {
    Text(text = "Loading...", color = Color.Black)
  }
}

@Composable
fun EventRow(event: Event, navigation: NavigationActions) {
  Row(
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 10.dp).clickable {
            val gson = Gson()
            val eventJson = gson.toJson(event)
            navigation.controller.navigate("event/$eventJson")
          },
      verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
          Image(
              bitmap = event.images ?: ImageBitmap(120, 120, config = ImageBitmapConfig.Rgb565),
              contentDescription = null)
        }

        Column(modifier = Modifier.weight(1f).padding(end = 1.dp)) {
          Text(
              text =
                  "Start date: ${event.eventStartDate?.
                format(DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT))}",
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp)
          Text(
              text =
                  "End date: ${event.eventEndDate?.
                format(DateTimeFormatter.ofPattern(EventFirebaseConnection.DATE_FORMAT))}",
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp)
          Text(text = event.title, fontSize = 14.sp)
        }

        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            when (event.eventStatus) {
              EventStatus.CREATED -> Text("Planned", color = Color(0xFF00668A), fontSize = 14.sp)
              EventStatus.ON_GOING -> Text("On going", color = Color(255, 165, 0), fontSize = 14.sp)
              EventStatus.DRAFT -> Text("Draft", color = Color(0xFF1FC959), fontSize = 14.sp)
              EventStatus.COMPLETED -> Text("Completed", color = Color.Gray, fontSize = 14.sp)
            }
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier.width(24.dp).height(24.dp).clickable {})
          }
        }
      }
  Divider(color = Color.Black, thickness = 1.dp)
}
