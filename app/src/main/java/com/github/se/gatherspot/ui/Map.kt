package com.github.se.gatherspot.ui

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.R
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.MapViewModel
import com.github.se.gatherspot.model.event.Event
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

private const val DEFAULT_ZOOM_LEVEL = 15f

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Map(viewModel: MapViewModel, nav: NavigationActions) {

    var init = true

    LaunchedEffect(init) {
        viewModel.events = viewModel.fetchEvents()
        init = false
    }


    val currentLocation by viewModel.currentLocation.observeAsState(LatLng(0.0, 0.0))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(currentLocation)
            .zoom(DEFAULT_ZOOM_LEVEL)
            .build()
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
          modifier = Modifier.testTag("Google Map"),
          contentPadding = paddingValues,
          cameraPositionState = cameraPositionState,
      ) {
          Marker(
              state = MarkerState(currentLocation),
              title = "Your current position"
          )
          for (event in viewModel.events) {
              Marker(
                  state = MarkerState(LatLng(event?.location?.latitude?:0.0, event?.location?.longitude?:0.0)),
                  title = event?.title?: "Event"
              )
          }
      }


  }
}
