package com.github.se.gatherspot.network

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.github.se.gatherspot.MainActivity


class NearbyConnect {
    /* Handle the permissions required by the nearby share service */
    private val PERMISSION_REQUEST_CODE = 85746312 // an integer to identify the permission request
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_SCAN
    )

    fun listUngrantedPermissions(context: Context): Array<String> {
        var ungrantedPermissionsList: Array<String> = emptyArray()
        for(permission in REQUIRED_PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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
            context as MainActivity,
            listUngrantedPermissions(context),
            PERMISSION_REQUEST_CODE
        )
    }

    /* nearby share functionalities */
}