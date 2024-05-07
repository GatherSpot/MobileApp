package com.github.se.gatherspot.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.app.ActivityCompat
import com.github.se.gatherspot.MainActivity
import com.github.se.gatherspot.ui.navigation.BottomNavigationMenu
import com.github.se.gatherspot.ui.navigation.NavigationActions
import com.github.se.gatherspot.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private const val DEFAULT_ZOOM_LEVEL = 15f

@Composable
fun MapViewContainer(mapView: MapView) {
  LaunchedEffect(MainActivity.mapAccess) {
    Log.d("MapAccess", MainActivity.mapAccess.toString())
    MainActivity.mapLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun getUserLocation(): LatLng? {
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
  var location: LatLng? = null
  if (ActivityCompat.checkSelfPermission(
      LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED &&
      ActivityCompat.checkSelfPermission(
          LocalContext.current, Manifest.permission.ACCESS_COARSE_LOCATION) !=
          PackageManager.PERMISSION_GRANTED) {
    return null
  } else {
    fusedLocationClient.lastLocation.addOnSuccessListener { l: Location? ->
      if (l != null && l.elapsedRealtimeAgeMillis < 1000 * 60 * 5) {
        location = LatLng(l.latitude, l.longitude)
      } else if (l != null && (l.elapsedRealtimeAgeMillis) >= 1000 * 60 * 5) {
        fusedLocationClient
            .getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location = LatLng(it.latitude, it.longitude) }
      } else {
        Log.d("Location", "Location is null")
      }
    }
  }

  return location
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Map(nav: NavigationActions) {

  val center = remember { mutableStateOf(LatLng(0.0, 0.0)) }
  center.value = getUserLocation() ?: LatLng(0.0, 0.0)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(center.value, DEFAULT_ZOOM_LEVEL)
  }
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tld -> nav.navigateTo(tld) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = nav.controller.currentBackStackEntry?.destination?.route)
      }) { paddingValues ->
        MapViewContainer(MapView(LocalContext.current))
        GoogleMap(
            properties = MapProperties(mapType = MapType.HYBRID),
            modifier = Modifier.testTag("Google Map"),
            contentPadding = paddingValues,
            cameraPositionState = cameraPositionState,
        ) {
          Marker(
              state = MarkerState(getUserLocation() ?: LatLng(0.0, 0.0)),
              title = "Your current position")
        }
      }
}
