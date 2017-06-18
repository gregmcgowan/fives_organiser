package com.gregmcgowan.fivesorganiser

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.gregmcgowan.fivesorganiser.players.PlayersActivity

class MainActivity : AppCompatActivity() {

    companion object {
        @JvmField val SHOW_PLAYERS = 1
    }
    val playersButton: View by find<View>(R.id.home_players_button);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        playersButton.setOnClickListener({ showPlayer() });
    }

    private fun showPlayer() {
        startActivityForResult(Intent(this, PlayersActivity::class.java), SHOW_PLAYERS)
    }
}
