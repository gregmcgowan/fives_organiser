package com.gregmcgowan.fivesorganiser.core

import android.support.annotation.ArrayRes
import android.support.annotation.StringRes

interface Strings {

    fun getString(@StringRes stringResId: Int): String

    fun getString(@StringRes stringResId: Int, vararg args: Any): String

    fun getStringArray(@ArrayRes arrayRes: Int): Array<String>


}
