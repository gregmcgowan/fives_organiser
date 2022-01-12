package com.gregmcgowan.fivesorganiser.core.permissions

import android.content.Context
import com.gregmcgowan.fivesorganiser.core.hasPermission

class Permission constructor(private val context: Context,
                             private val permissionName: String) {
    fun hasPermission(): Boolean = context.hasPermission(permissionName)
}

