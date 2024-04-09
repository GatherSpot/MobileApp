package com.github.se.gatherspot.ui

import android.content.ContentValues.TAG
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.EventsViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.model.event.EventStatus
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp as dp


/** Composable that displays events **/

@Composable
fun Events(viewModel: EventsViewModel, nav: NavigationActions) {
    val state = viewModel.uiState.collectAsState()
    var previousScrollPosition by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){
            Text(text = "Filter events", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Icon(imageVector = Icons.Filled.Menu, contentDescription = null,
                modifier = Modifier.clickable {})
        } },
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { tld -> nav.navigateTo(tld) },
                tabList = TOP_LEVEL_DESTINATIONS,
                selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
        }) {
            paddingValues ->
                val events = state.value.list
                val lazyState = rememberLazyListState()
                when {
                    events.isEmpty() -> Empty()
                    else -> {
                        Log.d(TAG, "do i even enter this??, ${events.size}")
                        LazyColumn(state = lazyState,
                            modifier = Modifier.padding(paddingValues)) {
                            items(events) { event ->
                                EventRow(event, nav)
                            }
                        }

                        LaunchedEffect(lazyState.isScrollInProgress){
                            val isAtBottom =
                                (lazyState.firstVisibleItemIndex + viewModel.PAGESIZE)  >=
                                        events.size
                            val currentScrollPosition = lazyState.firstVisibleItemScrollOffset
                            val downwards =
                                currentScrollPosition >= previousScrollPosition && currentScrollPosition > 0
                            previousScrollPosition = currentScrollPosition
                            if (lazyState.isScrollInProgress && isAtBottom && downwards) {
                                delay(1000)
                                viewModel.fetchNext()
                            }
                        }
                    }

                }
    }
}

@Composable
fun Empty(){
    Row(modifier = Modifier.padding(vertical = 40.dp)) {
        Text(text = "Loading...", color = Color.Black)
     //   Text(text ="Create One !", color = Color.Blue, modifier = Modifier.clickable {})
    }
}

@Composable
fun EventRow(event: Event, navigation: NavigationActions){
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable {
                navigation.controller.navigate("")
            },
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp)) {
            Text(text = event.eventStartDate.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = event.eventEndDate.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = event.title, fontSize = 16.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (event.eventStatus) {
                    EventStatus.CREATED -> Text("Planned", color = Color(0xFF00668A), fontSize = 13.sp)
                    EventStatus.ON_GOING -> Text("On going", color = Color(255, 165, 0))
                    EventStatus.DRAFT -> Text("Draft", color = Color(0xFF1FC959))
                    EventStatus.COMPLETED -> Text("Completed", color = Color.Gray)
                }
                Icon(
                    painter = painterResource(R.drawable.backarrow),
                    contentDescription = null,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp))
            }
        }
    }
    Divider(color = Color.Gray, thickness = 1.dp)
}