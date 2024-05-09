package com.github.se.gatherspot.ui

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

private const val DEFAULT_ZOOM_LEVEL = 15f

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Map(nav: NavigationActions) {

  val viewModel = MainActivity.mapViewModel!!

  val currentLocation by viewModel.currentLocation.observeAsState(LatLng(0.0, 0.0))

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.Builder().target(currentLocation).zoom(DEFAULT_ZOOM_LEVEL).build()
  }

  LaunchedEffect(nav.controller.currentBackStackEntry) {
    Log.d("Map", "LaunchedEffect1")
    viewModel.fetchEvents()
  }
  LaunchedEffect(key1 = Unit) {
    while (true) {
      viewModel.fetchLocation()
      delay(1000)
    }
  }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        LaunchedEffect(MainActivity.mapAccess) {
          Log.d("MapAccess", MainActivity.mapAccess.toString())
          MainActivity.mapLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        GoogleMap(
            properties = MapProperties(mapType = MapType.HYBRID),
            modifier = Modifier.testTag("GoogleMap"),
            contentPadding = paddingValues,
            cameraPositionState = cameraPositionState,
        ) {
          Marker(
              state = MarkerState(currentLocation),
              title = "Your current position",
              icon = BitmapDescriptorFactory.fromResource(R.drawable.person_pin))
          for (event in viewModel.events) {
            Marker(
                state =
                    MarkerState(
                        LatLng(
                            event?.location?.latitude ?: 0.0, event?.location?.longitude ?: 0.0)),
                title = event?.title ?: "Event",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.pin))
          }
        }
      }
}
