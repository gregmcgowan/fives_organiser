package com.gregmcgowan.fivesorganiser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.gregmcgowan.fivesorganiser.authenication.Authentication
import com.gregmcgowan.fivesorganiser.players.PlayerListContract
import com.gregmcgowan.fivesorganiser.players.PlayerListView
import com.gregmcgowan.fivesorganiser.players.PlayerListViewPresenter
import com.gregmcgowan.fivesorganiser.players.import.ImportContactsActivity

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val IMPORT_CONTACTS = 1
    }

    lateinit var playerListViewPresenter: PlayerListContract.Presenter
    val fab: FloatingActionButton by find<FloatingActionButton>(R.id.fab)
    val playerListView: PlayerListView by find<PlayerListView>(R.id.player_list_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(find<Toolbar>(R.id.toolbar).value)
        fab.setOnClickListener({
            getContacts()
        })
        playerListViewPresenter = PlayerListViewPresenter(playerListView, getApp()
                .dependencies.playersRepo)

        getApp().dependencies.authentication.signInAnonymously(object : Authentication.AuthenticationStateListener {
            override fun authStateChanged() {
                playerListViewPresenter.startPresenting()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        playerListViewPresenter.stopPresenting()
    }

    private fun getContacts() {
        startActivityForResult(Intent(this, ImportContactsActivity::class.java), IMPORT_CONTACTS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            playerListViewPresenter.startPresenting()
        }
    }
}
