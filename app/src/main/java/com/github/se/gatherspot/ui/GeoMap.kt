package com.github.se.gatherspot.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

data class GeoMapInterestPoint(val latLng: LatLng, val title: String)

@Composable
fun GeoMap(
    userCoordinates: LatLng?,
    interestsCoordinates: List<GeoMapInterestPoint>,
    mapViewModifier: Modifier = Modifier
) {
  // find the camera initial position (default is Rolex Learning Center at EPFL)
  var cameraCenter = LatLng(46.518567549767575, 6.568562923656716)

  if (userCoordinates != null) {
    cameraCenter = userCoordinates
  } else {
    // compute the middle geolocation of the points
    // so that they are all visible on the map
    if (interestsCoordinates.isNotEmpty()) {
      val latOnly = interestsCoordinates.map { it.latLng.latitude }
      val minLat = latOnly.min()
      val maxLat = latOnly.max()
      val centerLat = (minLat + maxLat) / 2

      val longOnly = interestsCoordinates.map { it.latLng.longitude }
      val minLong = longOnly.min()
      val maxLong = longOnly.max()
      val centerLong = (minLong + maxLong) / 2

      cameraCenter = LatLng(centerLat, centerLong)
    }
  }

  // default zoom level
  var zoom = 15f

  // define the camera position and zoom so that all points are visible with the minimal zoom level
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(cameraCenter, zoom)
  }

  Box(modifier = mapViewModifier) {
    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapType = MapType.HYBRID),
        modifier = mapViewModifier.testTag("Google Map")) {
          if (userCoordinates != null) {
            Marker(state = MarkerState(position = userCoordinates), title = "Your current position")
          }

          for (interest in interestsCoordinates) {
            Marker(state = MarkerState(position = interest.latLng), title = interest.title)
          }
        }
  }
}
