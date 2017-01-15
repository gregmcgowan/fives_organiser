package com.gregmcgowan.fivesorganiser

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import com.gregmcgowan.fivesorganiser.authenication.FirebaseAuthentication
import com.gregmcgowan.fivesorganiser.players.*

class MainActivity : AppCompatActivity() {

    var playerListViewPresenter: PlayerListContract.Presenter? = null
    val authentication = FirebaseAuthentication(FirebaseAuth.getInstance())
    val playersRepo = PlayersFirebaseRepo(FirebaseDatabase.getInstance(), authentication)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton?
        fab!!.setOnClickListener({ view ->
            playerListViewPresenter?.handleAddPlayerSelected()
        })

        val playerListView = findViewById(R.id.player_list_view) as PlayerListView
        playerListViewPresenter = PlayerListViewPresenter(playerListView, playersRepo)

        authentication.signInAnonymously(object : Authentication.AuthenticationStateListener{
            override fun authStateChanged() {
                playerListViewPresenter?.startPresenting()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
