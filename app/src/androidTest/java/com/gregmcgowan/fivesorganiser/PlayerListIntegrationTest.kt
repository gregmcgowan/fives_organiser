package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gregmcgowan.fivesorganiser.authentication.Authentication
import com.gregmcgowan.fivesorganiser.authentication.AuthenticationBindings
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import com.gregmcgowan.fivesorganiser.main.MainActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(
        AuthenticationBindings::class,
        PlayerRepoModule::class
)
@RunWith(AndroidJUnit4::class)
class PlayerListIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Bind value so we can set player data in the fake
    @BindValue
    @JvmField
    val repo: PlayerRepo = FakePlayerRepo()

    @Test
    fun testPlayersShown() {
        setPlayers(
                listOf(Player("1,", "greg", "1", "2", 1))
        )
        // Simple verification for now. As we are mostly making sure nothing crashes
        composeTestRule.onNodeWithText("greg")
                .assertIsDisplayed()
    }

    private fun setPlayers(players: List<Player>) {
        with(repo as FakePlayerRepo) {
            this.players = players.toMutableList()
            this.emitPlayers()
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface FakeAuthenticationBindings {

        @Binds
        @Singleton
        fun bind(impl: FakeAuthentication): Authentication

    }
}



