package com.github.se.gatherspot.network

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.github.se.gatherspot.MainActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy


class NearbyConnect {
  /* Handle the permissions required by the nearby share service */
  private val PERMISSION_REQUEST_CODE = 85746312 // an integer to identify the permission request
  private val REQUIRED_PERMISSIONS =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          arrayOf(
              android.Manifest.permission.BLUETOOTH_SCAN,
              android.Manifest.permission.BLUETOOTH_ADVERTISE,
              android.Manifest.permission.BLUETOOTH_CONNECT,
              android.Manifest.permission.ACCESS_FINE_LOCATION
          )
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
      } else {
          arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION)
      }

  fun listUngrantedPermissions(context: Context): Array<String> {
    var ungrantedPermissionsList: Array<String> = emptyArray()
    for (permission in REQUIRED_PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(context, permission) !=
          PackageManager.PERMISSION_GRANTED) {
        Log.d("ungranted permission", permission)
        ungrantedPermissionsList += permission
      }
    }
    return ungrantedPermissionsList
  }

  fun arePermissionGranted(context: Context): Boolean {
    return listUngrantedPermissions(context).isEmpty()
  }

  fun askForPermissions(context: Context) {
    ActivityCompat.requestPermissions(
        context as MainActivity, listUngrantedPermissions(context), PERMISSION_REQUEST_CODE)
  }

  /* nearby share functionalities */
    private val CONNECTION_STRATEGY = Strategy.P2P_POINT_TO_POINT
    private val SERVICE_ID = "com.github.gatherspot.ticket_system"

    fun scanTicket(context: Context, organiserId: String, markUserPresence: (String) -> Unit) {
        Log.d("scanning event ticket", "organiserId: $organiserId")

      // advertise the scanning of tickets
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(CONNECTION_STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                organiserId, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
            .addOnSuccessListener({})
        .addOnFailureListener({})
    }

    fun shareTicket(context: Context, organiserId: String, ticketData: String) {
        Log.d("sharing event ticket", "organiserId: $organiserId, ticketData: $ticketData")

        // search for the organiser's device
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(CONNECTION_STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> Log.d("ticket share error", "couldn't share text") }
    }
}
