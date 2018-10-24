package com.gregmcgowan.fivesorganiser.core.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun Activity.getPermissionResults(permission: Permission,
                                  results: PermissionResults,
                                  requestCode: Int,
                                  permissions: Array<out String>,
                                  grantResults: IntArray) {

    if (requestCode == permission.permissionRequestCode) {
        val permissionIndex = permissions.indexOf(permission.permissonName)
        if (grantResults[permissionIndex] == PackageManager.PERMISSION_GRANTED) {
            results.onPermissionGranted()
        } else {
            val userSaidNever = !ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission.permissonName)
            if (userSaidNever) {
                // user checked "never ask again"
                results.onPermissionDenied(true)
            } else {
                // we can show reasoning why
                results.onPermissionDenied(false)
            }
        }
    }
}

fun Activity.requestPermission(permission: Permission) {
    ActivityCompat.requestPermissions(this,
            arrayOf(permission.permissonName), permission.permissionRequestCode)
}
