package com.github.se.gatherspot.ui.topLevelDestinations

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.LiveData
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.model.MapViewModel
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

private const val DEFAULT_ZOOM_LEVEL = 15f
val buttonClicked = mutableStateOf(false)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Map(nav: NavigationActions, testPosition: LatLng? = null) {

  if (testPosition != null) {
    MapComposable(
        null,
        nav,
        rememberCameraPositionState {
          position = CameraPosition.Builder().target(testPosition).zoom(DEFAULT_ZOOM_LEVEL).build()
        },
        null,
        testPosition)
  } else {
    val viewModel = MainActivity.mapViewModel!!
    val currentLocation by viewModel.currentLocation.observeAsState(LatLng(0.0, 0.0))

    viewModel.cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.Builder().target(currentLocation).zoom(DEFAULT_ZOOM_LEVEL).build()
    }
    viewModel.cameraPositionState

    LaunchedEffect(nav.controller.currentBackStackEntry) { viewModel.fetchEvents() }
    LaunchedEffect(key1 = Unit) {
      while (true) {
        viewModel.fetchLocation()
        delay(1000)
      }
    }
    LaunchedEffect(key1 = Unit) {
      while (currentLocation == LatLng(0.0, 0.0)) {
        delay(500)
      }
      if (MainActivity.savedCameraPositionState != null) {
        viewModel.cameraPositionState = MainActivity.savedCameraPositionState!!
      } else {
        viewModel.cameraPositionState.position =
            CameraPosition.Builder().target(currentLocation).zoom(DEFAULT_ZOOM_LEVEL).build()
      }
      while (true) {
        if (viewModel.cameraPositionState.position.zoom > 12f) viewModel.fetchEvents()
        delay(1000)
      }
    }

    LaunchedEffect(key1 = buttonClicked) {
      if (buttonClicked.value) {
        viewModel.cameraPositionState =
            CameraPositionState(
                CameraPosition.Builder().target(currentLocation).zoom(DEFAULT_ZOOM_LEVEL).build())
        buttonClicked.value = false
      }
    }

    MapComposable(viewModel, nav, viewModel.cameraPositionState, viewModel.currentLocation, null)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MapComposable(
    viewModel: MapViewModel?,
    nav: NavigationActions,
    cameraPositionState: CameraPositionState,
    currentLocation: LiveData<LatLng>?,
    testPosition: LatLng?
) {
  Scaffold(
      topBar = { TopAppBar(title = { Text(text = "Map") }, modifier = Modifier.testTag("topBar")) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      },
      floatingActionButton = {
        IconButton(
            onClick = {
              buttonClicked.value = true
              viewModel!!.cameraPositionState =
                  CameraPositionState(
                      CameraPosition.Builder()
                          .target(viewModel.currentLocation.value ?: LatLng(0.0, 0.0))
                          .zoom(DEFAULT_ZOOM_LEVEL)
                          .build())
            },
            modifier = Modifier.testTag("positionButton")) {
              Icon(Icons.Filled.Home, contentDescription = "Go to current location")
            }
      },
      floatingActionButtonPosition = FabPosition.Center) { paddingValues ->
        if (testPosition == null) {
          LaunchedEffect(MainActivity.mapAccess) {
            Log.d("MapAccess", MainActivity.mapAccess.toString())
            MainActivity.mapLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            viewModel!!.fetchLocation()
          }
        }
        GoogleMap(
            properties = MapProperties(mapType = MapType.HYBRID),
            modifier = Modifier.testTag("GoogleMap"),
            contentPadding = paddingValues,
            cameraPositionState = cameraPositionState,
        ) {
          Log.d("ZoomLevel", "Current Zoom Level: ${cameraPositionState.position.zoom}")

          if (testPosition == null) {
            Marker(
                state = MarkerState(currentLocation?.value ?: LatLng(0.0, 0.0)),
                title = "Your current position",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.person_pin))

            for (event in
                viewModel!!.events.toSet().subtract(viewModel.registered_events.toList().toSet())) {
              Marker(
                  state =
                      MarkerState(
                          LatLng(
                              event?.location?.latitude ?: 0.0, event?.location?.longitude ?: 0.0)),
                  title = (event?.title),
                  icon = BitmapDescriptorFactory.fromResource(R.drawable.pin),
                  onInfoWindowClick = {
                    MainActivity.savedCameraPositionState = cameraPositionState
                    nav.controller.navigate("event/${event?.toJson()}")
                  },
                  snippet = (event?.description ?: ""),
                  visible = cameraPositionState.position.zoom > 12f)
            }
            for (event in (viewModel.registered_events.toSet())) {
              Marker(
                  state =
                      MarkerState(
                          LatLng(
                              event?.location?.latitude ?: 0.0, event?.location?.longitude ?: 0.0)),
                  title = ("Registered to " + event?.title),
                  icon = BitmapDescriptorFactory.fromResource(R.drawable.target),
                  onInfoWindowClick = { nav.controller.navigate("event/${event?.toJson()}") },
                  snippet = (event?.description ?: ""),
                  visible = cameraPositionState.position.zoom > 12f)
            }
          } else {
            Marker(
                state = MarkerState(testPosition),
                title = "Your current position",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.person_pin))
          }
        }
      }
}
