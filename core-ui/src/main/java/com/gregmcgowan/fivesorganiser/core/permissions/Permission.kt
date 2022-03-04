package com.gregmcgowan.fivesorganiser.core.permissions

import android.content.Context
import com.gregmcgowan.fivesorganiser.core.hasPermission

interface Permission {
    val name: String

    fun hasPermission(): Boolean
}

class AndroidPermission constructor(private val context: Context,
                                    private val permissionName: String) : Permission {
    override val name: String
        get() = permissionName

    override fun hasPermission(): Boolean = context.hasPermission(name)
}

