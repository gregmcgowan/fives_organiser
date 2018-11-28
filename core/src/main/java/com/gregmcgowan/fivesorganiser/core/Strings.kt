package com.gregmcgowan.fivesorganiser.core

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

@StringRes const val NO_STRING_RES_ID = -1

interface Strings {

    fun getString(@StringRes stringResId: Int): String

    fun getString(@StringRes stringResId: Int, vararg args: Any): String

    fun getStringArray(@ArrayRes arrayRes: Int): Array<String>


}
