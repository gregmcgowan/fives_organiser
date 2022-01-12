package com.gregmcgowan.fivesorganiser.core.permissions

import android.content.Context
import com.gregmcgowan.fivesorganiser.core.hasPermission

class Permission constructor(val context: Context,
                             val permissonName: String) {
    fun hasPermission(): Boolean = context.hasPermission(permissonName)
}

