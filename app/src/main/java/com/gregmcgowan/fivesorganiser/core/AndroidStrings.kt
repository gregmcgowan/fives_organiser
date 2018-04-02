package com.gregmcgowan.fivesorganiser.core

import android.content.res.Resources
import javax.inject.Inject

class AndroidStrings @Inject constructor(val resources: Resources) : Strings {

    override fun getString(stringResId: Int): String = resources.getString(stringResId)

    @SuppressWarnings("SpreadOperator")
    override fun getString(stringResId: Int, vararg args: Any): String =
            resources.getString(stringResId, *args)

    override fun getStringArray(arrayRes: Int): Array<String> = resources.getStringArray(arrayRes)

}