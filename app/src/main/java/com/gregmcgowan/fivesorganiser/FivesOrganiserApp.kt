package com.gregmcgowan.fivesorganiser

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gregmcgowan.fivesorganiser.core.authenication.FirebaseAuthentication
import com.gregmcgowan.fivesorganiser.core.data.FirestoreHelper
import com.gregmcgowan.fivesorganiser.core.data.match.MatchFirebaseRepo
import com.gregmcgowan.fivesorganiser.core.data.player.PlayersFirebaseRepo
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class FivesOrganiserApp : Application() {

    //TODO proper dependency injection
    lateinit var dependencies: Dependencies

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)

        val firebaseAuthentication = FirebaseAuthentication(FirebaseAuth.getInstance())
        val database = FirestoreHelper(
                authentication = firebaseAuthentication,
                firebaseFirestore = FirebaseFirestore.getInstance()
        )

        dependencies = Dependencies(
                firebaseAuthentication,
                PlayersFirebaseRepo(database),
                MatchFirebaseRepo(database)
        )
    }
}