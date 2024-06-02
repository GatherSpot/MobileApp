package com.github.se.gatherspot.network

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.se.gatherspot.MainActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
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
                organiserId,
                SERVICE_ID,
                MyConnectionLifeCycleCallback(true, null, markUserPresence),
                advertisingOptions
            )
            .addOnSuccessListener({})
        .addOnFailureListener({})
    }

    fun shareTicket(context: Context, organiserId: String, ticketData: String) {
        Log.d("sharing event ticket", "organiserId: $organiserId, ticketData: $ticketData")
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(CONNECTION_STRATEGY).build()

        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, MyEndpointDiscoveryCallback(organiserId, ticketData), discoveryOptions)
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> Log.d("ticket share error", "couldn't share text") }
    }

    private class MyEndpointDiscoveryCallback(
        val organiserId: String,
        val ticketData: String,
        val callback: (String) -> Unit = {}
    ): EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, p1: DiscoveredEndpointInfo) {
            Log.d("EndpointDiscovery", "endpoint found")
            if(endpointId == organiserId) {
                Nearby.getConnectionsClient(
                    getApplicationContext()
                ).requestConnection(
                    "deviceName",
                    endpointId,
                    MyConnectionLifeCycleCallback(false, ticketData, callback)
                ).addOnSuccessListener {
                    // 5
                    Log.d("connection", "Successfully requested a connection")
                }.addOnFailureListener {
                    // 6
                    Log.d("connection", "Failed to request the connection")
                }
            }
        }

        override fun onEndpointLost(p0: String) {
            Log.d("EndpointDiscovery", "endpoint lost")
        }
    }

    private class MyConnectionLifeCycleCallback(
        val isOrganiserCallback: Boolean,
        val ticketData: String?,
        val callback: (String) -> Unit = {}
    ): ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, p1: ConnectionInfo) {
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endpointId, MyPayloadCallback(isOrganiserCallback, ticketData, callback))
        }
        override fun onConnectionResult(endpoint: String, p1: ConnectionResolution) {
            when (p1.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d("Advertiser connection successful", "ConnectionsStatusCodes.STATUS_OK")
                    if(!isOrganiserCallback){
                        Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpoint, Payload.fromBytes(ticketData!!.toByteArray()))
                    }
                }
                else -> {
                    Log.d(
                        "Advertiser connection failed",
                        "Connection failed. Received status $p1.status"
                    )
                }
            }
        }
        override fun onDisconnected(p0: String) {
            Log.d("Connection", "peripheral disconnected")
        }
    }

    private class MyPayloadCallback(
        val isOrganiserCallback: Boolean,
        val ticketData: String?,
        val callback: (String) -> Unit = {}
    ): PayloadCallback() {
        override fun onPayloadReceived(p0: String, payload: Payload) {
            if(isOrganiserCallback) {
                if (payload.type == Payload.Type.BYTES) {
                    callback(payload.asBytes()?.toString(Charsets.UTF_8) ?: "")
                }else{
                    Log.d("PayloadCallback", "payload type is not BYTES")
                }
            }
            Log.d("PayloadCallback", "onPayloadReceived")
        }
        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            Log.d("PayloadCallback", "onPayloadTransferUpdate")
        }
    }
}
