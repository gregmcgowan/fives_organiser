package com.gregmcgowan.fivesorganiser

import android.app.Application
import com.google.firebase.FirebaseApp
import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.DaggerAppComponent
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class FivesOrganiserApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)

        appComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build()

    }
}