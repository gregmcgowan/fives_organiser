package com.gregmcgowan.fivesorganiser

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gregmcgowan.fivesorganiser.authenication.FirebaseAuthentication
import com.gregmcgowan.fivesorganiser.players.PlayersFirebaseRepo


class FivesOrganiserApp : Application() {

    //TODO proper dependency injection

    lateinit var dependacies: Dependancies

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val firebaseAuthentication = FirebaseAuthentication(FirebaseAuth.getInstance())

        dependacies = Dependancies(firebaseAuthentication,
                PlayersFirebaseRepo(FirebaseDatabase.getInstance(), firebaseAuthentication)
        )
    }
}