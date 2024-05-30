package com.github.se.gatherspot.ui.topLevelDestinations

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.RatingFirebaseConnection
import com.github.se.gatherspot.model.Interests
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.getEventIcon
import com.github.se.gatherspot.sql.AppDatabase
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

/**
 * Composable for the events screen.
 *
 * @param viewModel The view model for the events view
 * @param nav The navigation actions
 */
@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Events(viewModel: EventsViewModel, nav: NavigationActions) {

  val pagerState = rememberPagerState(pageCount = { viewModel.tabList.size }, initialPage = 1)
  val showInterestsDialog = viewModel.showFilterDialog.observeAsState(false).value
  val showDialog = viewModel::showDialog
  val applyFilter = viewModel::applyFilter
  val revertFilter = viewModel::revertFilter
  val removeFilter = viewModel::removeFilter
  Scaffold(
      modifier = Modifier.testTag("EventsScreen"),
      topBar = { EventTypeTab(viewModel.tabList, pagerState) },
      floatingActionButton = { CreateAndFilterButton(nav) { showDialog() } },
      floatingActionButtonPosition = FabPosition.End,
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        if (showInterestsDialog) {
          InterestsDialog(
              MainActivity.selectedInterests.observeAsState(),
              applyFilter,
              revertFilter,
              removeFilter)
        }
        // Box for content to add the topbar and bottombar padding automatically
        Box(modifier = Modifier.padding(paddingValues)) {
          // Main content
          Pager(viewModel, nav, pagerState)
        }
      }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventList(
    vm: EventsViewModel,
    fetch: () -> Unit,
    fetching: State<Boolean>,
    events: State<List<Event>>,
    nav: NavigationActions,
    testTag: String,
    isFeed: Boolean = false,
    refresh: () -> Unit =
        fetch, // in most cases we just rerun fetch, just different when we use with interests
) {
  val lazyState = rememberLazyListState()
  val pullRefreshState =
      rememberPullRefreshState(refreshing = fetching.value, onRefresh = { refresh() })
  // lines to add functionality to fetch next when we reach end of list
  fun LazyListState.isScrolledToEnd() =
      layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
  val endOfListReached by remember {
    derivedStateOf {
      // this if is used to make sure we don't re-fetch the same thing for no reason repeatedly on
      // other screens than feed
      if (isFeed) lazyState.isScrolledToEnd() else false
    }
  }
  Log.d(TAG, "size = " + (events.value.size).toString())
  Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
    if (events.value.isEmpty()) Empty(vm, MainActivity.selectedInterests, fetch)
    else {
      LazyColumn(
          state = lazyState, modifier = Modifier.padding(vertical = 15.dp).testTag(testTag)) {
            items(events.value) { event ->
              EventItem(event, vm::getEventTiming, vm::isOrganizer, vm::isRegistered, nav)
            }
          }
      LaunchedEffect(endOfListReached) {
        if (endOfListReached) fetch()
      } // condition avoids re-fetching on recomposition,
    }
    PullRefreshIndicator(
        refreshing = fetching.value,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter).testTag("fetching"))
  }
}

@Composable
private fun EventItem(
    event: Event,
    getEventTiming: (Event) -> EventsViewModel.EventTiming,
    isOrganizer: (Event) -> Boolean,
    isRegistered: (Event) -> Boolean,
    navigation: NavigationActions
) {
  var globalOrganizerRating by remember { mutableStateOf<Double?>(null) }
  val ratingFBC = RatingFirebaseConnection()
  LaunchedEffect(event.id) {
    Log.d(",", "calling func")
    globalOrganizerRating = ratingFBC.fetchOrganizerGlobalRating(event.organizerID)
  }
  Box(
      modifier =
          Modifier.background(
                  color =
                      when (getEventTiming(event)) {
                        EventsViewModel.EventTiming.PAST -> Color.LightGray
                        EventsViewModel.EventTiming.TODAY -> Color.Green
                        EventsViewModel.EventTiming.FUTURE -> Color.White
                      },
                  // maybe find a way to discern if it is ours in other way than the timing, as it
                  // becomes non intuitive
                  //            Color.LightGray
                  //          } else if (isToday) {
                  //            Color(255, 0, 0, 160)
                  //          } else if (isOrganizer) {
                  //            Color(80, 50, 200, 120)
                  //          } else if (isRegistered) {
                  //            Color(46, 204, 113, 120)
                  //          } else {
                  //            Color.White
                  //          },
                  shape = RoundedCornerShape(5.dp))
              .clickable { navigation.controller.navigate("event/${event.toJson()}") }
              .testTag("eventItem")
              .fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp)) {
              Row(
                  modifier = Modifier.weight(1f).testTag("IconHolder"),
                  horizontalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = getEventIcon(event.categories)),
                        contentDescription = "event icon",
                        modifier = Modifier.size(40.dp).testTag("EventIcon"))
                  }
              Column(modifier = Modifier.weight(1f).padding(end = 1.dp)) {
                Text(
                    text =
                        "Start date: ${
                        event.eventStartDate?.format(
                            DateTimeFormatter.ofPattern(
                                EventFirebaseConnection.DATE_FORMAT_DISPLAYED
                            )
                        )
                    }",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp)
                Text(
                    text =
                        "End date: ${
                        event.eventEndDate?.format(
                            DateTimeFormatter.ofPattern(
                                EventFirebaseConnection.DATE_FORMAT_DISPLAYED
                            )
                        )
                    }",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp)
                Text(
                    text = event.title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                when {
                  globalOrganizerRating != null ->
                      Text(
                          "Organizer Rating:$globalOrganizerRating/5.0",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis)
                  else -> {}
                }
              }

              Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (isOrganizer(event)) {
                    Text("Organizer", fontSize = 14.sp)
                  } else if (isRegistered(event)) {
                    Text("Registered", fontSize = 14.sp)
                  }

                  Icon(
                      painter = painterResource(R.drawable.arrow_right),
                      contentDescription = "go to event page",
                      modifier = Modifier.width(24.dp).height(24.dp).clickable {})
                }
              }
            }
        Divider(color = Color.Black, thickness = 1.dp)
      }
}

/**
 * Composable that displays a message when no events are loaded.
 *
 * @param viewModel The view model for the events view
 * @param interests The interests in the filter
 * @param fetch The function to call when refreshing the events
 */
@Composable
private fun Empty(
    viewModel: EventsViewModel,
    interests: MutableLiveData<Set<Interests>>,
    fetch: () -> Unit
) {

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
                  interests.value = Interests.new()
                })
        Text("or ")
        Text("try loading new ones", color = Color.Blue, modifier = Modifier.clickable { fetch() })
      }
    }
  }
}

@Composable
private fun InterestsDialog(
    selectedInterests: State<Set<Interests>?>,
    setFilter: () -> Unit,
    revertFilter: () -> Unit,
    resetFilter: () -> Unit
) {
  // TODO make functions to remove random side-effects from here
  AlertDialog(
      onDismissRequest = { revertFilter() },
      title = { Text("Select Interests") },
      modifier = Modifier.testTag("interestsDialog"),
      text = {
        val items = Interests.toList()
        LazyColumn {
          items(items) { item ->
            Row(
                Modifier.fillMaxWidth().testTag(item.name).clickable {
                  MainActivity.selectedInterests.value =
                      Interests.flipInterest(MainActivity.selectedInterests.value!!, item)
                }) {
                  Checkbox(
                      checked = selectedInterests.value!!.contains(item),
                      onCheckedChange =
                          null // We handle the checkbox toggle in the Row's clickable modifier
                      )
                  Text(text = item.name.toLowerCase(Locale.ROOT), Modifier.padding(start = 8.dp))
                }
          }
        }
      },
      confirmButton = {
        Button(onClick = { setFilter() }, modifier = Modifier.testTag("setFilterButton")) {
          Text("Confirm")
        }
      },
      dismissButton = {
        Button(onClick = { resetFilter() }, modifier = Modifier.testTag("removeFilter")) {
          Text("Remove Filter")
        }
      })
}

@Composable
private fun CreateAndFilterButton(nav: NavigationActions, showDialog: () -> Unit) {
  Column {
    Button(
        onClick = { showDialog() },
        modifier = Modifier.testTag("filterMenu"),
        content = {
          Icon(
              painter = painterResource(R.drawable.filter),
              contentDescription = "Filter",
              modifier = Modifier.size(30.dp))
        })
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = { nav.controller.navigate("createEvent") },
        modifier = Modifier.testTag("createMenu"),
        content = { Icon(Icons.Filled.Add, "Create new event", modifier = Modifier.size(30.dp)) })
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventTypeTab(tabList: List<String>, pagerState: PagerState) {
  val coroutineScope = rememberCoroutineScope()
  TabRow(selectedTabIndex = pagerState.currentPage) {
    tabList.forEachIndexed { index, title ->
      Tab(
          text = { Text(title) },
          selected = pagerState.currentPage == index,
          modifier = Modifier.testTag(title),
          onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } })
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pager(vm: EventsViewModel, nav: NavigationActions, pagerState: PagerState) {
  HorizontalPager(state = pagerState) { page ->
    when (page) {
      // fun EventList(vm: EventsViewModel, events: State<List<Event>>, nav: NavigationActions){
      0 ->
          EventList(
              vm,
              vm::fetchMyEvents,
              vm.fetchingMine.observeAsState(false),
              vm.myEvents.observeAsState(listOf()),
              nav,
              "myEventsList")
      1 ->
          EventList(
              vm,
              vm::fetchWithInterests,
              vm.fetching.observeAsState(false),
              vm.allEvents.observeAsState(listOf()),
              nav,
              "eventsList",
              true,
              vm::refresh)
      2 ->
          EventList(
              vm,
              vm::fetchUpComing,
              vm.fetchingUpcoming.observeAsState(false),
              vm.upComing.observeAsState(listOf()),
              nav,
              "upComingEventsList")
      3 ->
          EventList(
              vm,
              vm::fetchFromFollowedUsers,
              vm.fetchingFollowed.observeAsState(false),
              vm.fromFollowedUsers.observeAsState(listOf()),
              nav,
              "followedEventsList")
      4 ->
          EventList(
              vm,
              vm::fetchAttended,
              vm.fetchingAttended.observeAsState(false),
              vm.attended.observeAsState(listOf()),
              nav,
              "attendedEventsList")
      else -> throw IllegalStateException("Invalid page index")
    }
  }
}

// Preview for the Event UI, for testing purposes
@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
private fun EventUIPreview() {
  val nav = rememberNavController()
  val context = LocalContext.current
  val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
  val viewModel = EventsViewModel(db)
  Events(viewModel, NavigationActions(nav))
}
