package com.gregmcgowan.fivesorganiser.core

import android.support.v4.app.Fragment
import com.gregmcgowan.fivesorganiser.core.di.AppComponent

abstract class BaseFragment : Fragment() {

    val appComponent: AppComponent get() = getApp().appComponent

}