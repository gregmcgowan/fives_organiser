package com.gregmcgowan.fivesorganiser

import android.app.Application
import com.google.firebase.FirebaseApp
import com.gregmcgowan.fivesorganiser.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import timber.log.Timber

open class FivesOrganiserApp : Application() {
    val appGraph: AppGraph by lazy {
        createGraph()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FirebaseApp.initializeApp(this)
    }

    open fun createGraph(): AppGraph = createGraphFactory<AppGraph.Factory>().create(context = this, app = this)
}
