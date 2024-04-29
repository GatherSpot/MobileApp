package com.github.se.gatherspot.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.model.utils.LocalDateDeserializer
import com.github.se.gatherspot.model.utils.LocalDateSerializer
import com.github.se.gatherspot.model.utils.LocalDateTimeDeserializer
import com.github.se.gatherspot.model.utils.LocalDateTimeSerializer
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

/** Composable that displays events * */

// listOf("Your interests", "None")
@Composable
fun Events(viewModel: EventsViewModel, nav: NavigationActions) {

  val state = viewModel.uiState.collectAsState()
  var previousScrollPosition by remember { mutableIntStateOf(0) }
  var loading by remember { mutableStateOf(false) }
  var fetched by remember { mutableStateOf(false) }
  var showDropdownMenu by remember { mutableStateOf(false) }
  val filters = enumValues<Interests>().toList()
  var interestsSelected = mutableListOf<Interests>()
  var fetch by remember { mutableStateOf(false) }

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
                Box {
                  Icon(
                      imageVector = Icons.Filled.Menu,
                      contentDescription = null,
                      modifier =
                          Modifier.clickable {
                                showDropdownMenu = !showDropdownMenu
                                if (!showDropdownMenu) {
                                  viewModel.filter(interestsSelected)
                                } else {
                                  interestsSelected = mutableListOf()
                                }
                              }
                              .testTag("filterMenu"))
                  DropdownMenu(
                      modifier = Modifier.height(300.dp).testTag("dropdown"),
                      expanded = showDropdownMenu,
                      onDismissRequest = {
                        showDropdownMenu = false
                        viewModel.filter(interestsSelected)
                      }) {
                        DropdownMenuItem(
                            text = { Text("REMOVE FILTER") },
                            onClick = {
                              viewModel.removeFilter()
                              interestsSelected = mutableListOf()
                              showDropdownMenu = false
                            },
                            leadingIcon = { Icon(Icons.Filled.Clear, "clear") })

                        filters.forEach { s -> StatefulDropdownItem(s, interestsSelected) }
                      }
                }
                Spacer(modifier = Modifier.width(30.dp))
                Icon(
                    Icons.Filled.Refresh,
                    "fetch",
                    modifier = Modifier.clickable { fetch = true }.testTag("refresh"))
                LaunchedEffect(fetch) {
                  if (fetch) {
                    Log.d(TAG, "entered with $interestsSelected")
                    viewModel.fetchNext(interestsSelected)
                    fetch = false
                  }
                }

                Spacer(modifier = Modifier.width(30.dp))
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
        if (fetch) {
          Text(
              modifier = Modifier.testTag("fetch").padding(vertical = 40.dp),
              text = "Fetching new events matching: ${interestsSelected.joinToString(", ")}",
              fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        val events = state.value.list
        val lazyState = rememberLazyListState()
        Log.d(TAG, "size = " + events.size.toString())
        when {
          events.isEmpty() -> Empty(viewModel, interestsSelected) { fetch = true }
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
                viewModel.fetchNext(interestsSelected)
                fetched = true
              }
            }
          }
        }
      }
}

@Composable
fun EventRow(event: Event, navigation: NavigationActions) {
  val eventFirebaseConnection = com.github.se.gatherspot.EventFirebaseConnection()
  Row(
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 10.dp).clickable {
            // Create a new Gson instance with the custom serializers and deserializers
            val gson: Gson =
                GsonBuilder()
                    .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
                    .create()

            val eventJson = gson.toJson(event)
            Log.e("Display", "eventJson = $eventJson")
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
                format(DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT))}",
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp)
          Text(
              text =
                  "End date: ${event.eventEndDate?.
                format(DateTimeFormatter.ofPattern(eventFirebaseConnection.DATE_FORMAT))}",
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

@Composable
fun Empty(viewModel: EventsViewModel, interests: MutableList<Interests>, fetch: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().testTag("empty"), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text("No loaded events matched your query")
      Row {
        Text(
            "Remove filter ",
            color = Color.Blue,
            modifier =
                Modifier.clickable {
                  viewModel.removeFilter()
                  interests.removeAll { true }
                })
        Text("or ")
        Text("try loading new ones", color = Color.Blue, modifier = Modifier.clickable { fetch() })
      }
    }
  }
}

@Composable
fun StatefulDropdownItem(interest: Interests, interestsSelected: MutableList<Interests>) {
  var selected by remember { mutableStateOf(false) }

  DropdownMenuItem(
      modifier = Modifier.testTag(interest.toString()),
      text = { Text(interest.toString()) },
      onClick = {
        selected = !selected
        if (selected) {
          Log.d(TAG, "added ${interest.name}")
          interestsSelected.add(interest)
        } else {
          Log.d(TAG, "removed ${interest.name}")
          interestsSelected.remove(interest)
        }
      },
      leadingIcon = {
        if (selected) {
          Icon(Icons.Filled.Done, "")
        }
      },
  )
}
