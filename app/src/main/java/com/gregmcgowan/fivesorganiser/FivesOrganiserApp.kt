package com.gregmcgowan.fivesorganiser

import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ApplicationComponent
import timber.log.Timber


@HiltAndroidApp
class FivesOrganiserApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return EntryPoints.get(this, ApplicationInjector::class.java)
    }


}


@EntryPoint
@InstallIn(ApplicationComponent::class)
internal interface ApplicationInjector : AndroidInjector<FivesOrganiserApp>





