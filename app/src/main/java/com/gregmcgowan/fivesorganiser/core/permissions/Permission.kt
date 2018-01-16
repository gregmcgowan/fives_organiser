package com.gregmcgowan.fivesorganiser.core.permissions

import android.app.Activity
import com.gregmcgowan.fivesorganiser.core.hasPermission

class Permission(val activity: Activity,
                 val permissonName: String) {
    val permissionRequestCode: Int = 123

    fun hasPermission(): Boolean = activity.hasPermission(permissonName)

    fun requestPermission() {
        activity.requestPermission(this)
    }

}

fun Permission.checkPermission(granted: () -> Unit, denied: () -> Unit, request: Boolean) {
    if (hasPermission()) {
        granted()
    } else {
        denied()
        if (request) requestPermission()
    }
}
