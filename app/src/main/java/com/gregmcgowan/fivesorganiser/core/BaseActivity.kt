package com.gregmcgowan.fivesorganiser.core

import android.support.v7.app.AppCompatActivity
import com.gregmcgowan.fivesorganiser.core.di.AppComponent

abstract class BaseActivity : AppCompatActivity() {

    val appComponent: AppComponent get() = getApp().appComponent

}