package com.github.se.gatherspot.model

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.util.Log
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

@RequiresApi(Build.VERSION_CODES.S)
class MapViewModel(application: Application) : AndroidViewModel(application) {

  private val fusedLocationClient =
      LocationServices.getFusedLocationProviderClient(application.applicationContext)
  private val _currentLocation = MutableLiveData<LatLng>()

  private var _events = mutableListOf<Event?>()

  val currentLocation: LiveData<LatLng>
    get() = _currentLocation

  var events: MutableList<Event?>
    get() = _events
    set(value) {
      _events = value
    }

  init {
    fetchLocation()
  }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun fetchLocation() {
    Log.d("MapViewModel", "fetchLocation")
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

  suspend fun fetchEvents(): MutableList<Event?> {
    val list =
        IdListFirebaseConnection()
            .fetchFromFirebase(
                FirebaseAuth.getInstance().currentUser?.uid ?: "0",
                FirebaseCollection.REGISTERED_EVENTS) {}!!
            .events
            .toMutableList()
    return list.map { EventFirebaseConnection().fetch(it) }.toMutableList()
  }
}
