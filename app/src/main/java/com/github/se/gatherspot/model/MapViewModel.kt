package com.github.se.gatherspot.model

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.se.gatherspot.firebase.EventFirebaseConnection
import com.github.se.gatherspot.firebase.FirebaseCollection
import com.github.se.gatherspot.firebase.IdListFirebaseConnection
import com.github.se.gatherspot.model.event.Event
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.CameraPositionState
import kotlin.math.PI
import kotlin.math.cos

/**
 * ViewModel for the map.
 *
 * @property application Application The application.
 */
@RequiresApi(Build.VERSION_CODES.S)
class MapViewModel(application: Application) : AndroidViewModel(application) {

  companion object {

    val EARTH_RADIUS = 6371000 // Earth's radius in meters

    /**
     * Function to calculate distance change for a given angle at a latitude
     *
     * @param angle the angle in degrees
     * @param latitude the latitude in degrees
     * @return the distance change in meters
     */
    fun degreeToMeters(angle: Double, latitude: Double): Double {
      val latitudeRadians = Math.toRadians(latitude)
      val circumference =
          2 * PI * EARTH_RADIUS * cos(latitudeRadians) // Circumference of latitude circle

      // Calculate the distance change
      val distanceChange = (angle / 360) * circumference
      return distanceChange
    }

    /**
     * Function to calculate angle change for a given distance at a latitude
     *
     * @param distance the distance in meters
     * @param latitude the latitude in degrees
     * @return the angle change in degrees
     */
    fun metersToDegree(distance: Double, latitude: Double): Double {
      val latitudeRadians = Math.toRadians(latitude)
      val circumference =
          2 * PI * EARTH_RADIUS * cos(latitudeRadians) // Circumference of latitude circle

      // Calculate the angle change
      val angleChange = (distance / circumference) * 360
      return angleChange
    }
  }

  private val fusedLocationClient =
      LocationServices.getFusedLocationProviderClient(application.applicationContext)
  private val _currentLocation = MutableLiveData<LatLng>()

  private var _registered_events = mutableListOf<Event?>()
  private var _events = mutableListOf<Event?>()
  private var _cameraPositionState = CameraPositionState()

  val currentLocation: LiveData<LatLng>
    get() = _currentLocation

  var cameraPositionState: CameraPositionState
    get() = _cameraPositionState
    set(value) {
      _cameraPositionState = value
    }

  var events: MutableList<Event?>
    get() = _events
    set(value) {
      _events = value
    }

  var registered_events: MutableList<Event?>
    get() = _registered_events
    set(value) {
      _registered_events = value
    }

  init {
    fetchLocation()
  }

  /** Fetches and updates the location of the user. */
  @RequiresApi(Build.VERSION_CODES.S)
  fun fetchLocation() {
    if (ActivityCompat.checkSelfPermission(
        getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient
          .getCurrentLocation(LocationRequest.QUALITY_HIGH_ACCURACY, null)
          .addOnSuccessListener { location: Location? ->
            location?.let { _currentLocation.value = LatLng(it.latitude, it.longitude) }
          }
    }
  }

  /**
   * Fetches the events visible at the moment on the map, and stores them in the registered_events
   * and events lists.
   */
  suspend fun fetchEvents() {
    val list =
        IdListFirebaseConnection()
            .fetchFromFirebase(
                FirebaseAuth.getInstance().currentUser?.uid ?: "0",
                FirebaseCollection.REGISTERED_EVENTS)
            .elements
            .toMutableList()
    registered_events =
        registered_events.union(list.map { EventFirebaseConnection().fetch(it) }).toMutableList()

    val list2 =
        EventFirebaseConnection()
            .fetchAllInPerimeter(
                cameraPositionState.position.target.latitude,
                cameraPositionState.position.target.longitude,
                metersToDegree(1000.0, cameraPositionState.position.target.latitude))

    events = events.union(list2.toMutableList()).toMutableList()
  }
}
