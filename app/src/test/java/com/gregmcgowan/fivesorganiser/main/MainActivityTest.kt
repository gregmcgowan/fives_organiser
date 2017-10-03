package com.gregmcgowan.fivesorganiser.main

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.gregmcgowan.fivesorganiser.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    lateinit var buildActivity : Activity

    @Before
    fun setUp() {
        buildActivity = Robolectric.setupActivity(Activity::class.java)
    }

    @Test
    fun onCreate() {

        val playerList: Any = buildActivity.findViewById<RecyclerView>(R.id.player_list)
    }



}