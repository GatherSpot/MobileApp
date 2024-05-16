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
import kotlin.math.PI
import kotlin.math.cos

@RequiresApi(Build.VERSION_CODES.S)
class MapViewModel(application: Application) : AndroidViewModel(application) {

  fun degreeToMeters(latitudeDegrees: Double): Double {
    // Radius of the Earth at the equator in meters
    val earthRadiusAtEquator = 6378137.0 // in meters

    // Convert latitude from degrees to radians
    val latitudeRadians = Math.toRadians(latitudeDegrees)

    // Calculate the length of a degree of latitude in meters
    // This accounts for the Earth's curvature using the WGS-84 ellipsoid
    // Formula: length_of_degree = (2 * PI * radius) * (cos(latitude))
    // Where `latitude` is in radians and `radius` is the radius at that latitude
    val radiusAtLatitude = earthRadiusAtEquator * cos(latitudeRadians)
    val lengthOfDegree = (2 * PI * radiusAtLatitude) / 360.0

    return lengthOfDegree
  }

  fun metersToDegree(latitudeDegrees: Double, meters: Double): Double {
    // Radius of the Earth at the equator in meters
    val earthRadiusAtEquator = 6378137.0 // in meters

    // Convert latitude from degrees to radians
    val latitudeRadians = Math.toRadians(latitudeDegrees)

    // Calculate the length of a degree of latitude in meters
    // This accounts for the Earth's curvature using the WGS-84 ellipsoid
    // Formula: length_of_degree = (2 * PI * radius) * (cos(latitude))
    // Where `latitude` is in radians and `radius` is the radius at that latitude
    val radiusAtLatitude = earthRadiusAtEquator * cos(latitudeRadians)
    val lengthOfDegree = (2 * PI * radiusAtLatitude) / 360.0

    // Calculate the number of degrees corresponding to the given meters
    val degrees = meters / lengthOfDegree

    return degrees
  }

  private val fusedLocationClient =
      LocationServices.getFusedLocationProviderClient(application.applicationContext)
  private val _currentLocation = MutableLiveData<LatLng>()

  private var _registered_events = mutableListOf<Event?>()
  private var _events = mutableListOf<Event?>()

  val currentLocation: LiveData<LatLng>
    get() = _currentLocation

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

  suspend fun fetchEvents() {
    val list =
        IdListFirebaseConnection()
            .fetchFromFirebase(
                FirebaseAuth.getInstance().currentUser?.uid ?: "0",
                FirebaseCollection.REGISTERED_EVENTS) {}!!
            .events
            .toMutableList()
    registered_events = list.map { EventFirebaseConnection().fetch(it) }.toMutableList()

    val latitude = _currentLocation.value?.latitude ?: 0.0
    val longitude = _currentLocation.value?.longitude ?: 0.0

    val list2 =
        EventFirebaseConnection()
            .fetchAllInPerimeter(latitude, longitude, metersToDegree(latitude, 1000.0))

    events = list2.toMutableList()
  }
}
