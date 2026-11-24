package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerListIntegrationTest {
    init {
        FakePlayerRepoModule.fakePlayerRepo.players =
            mutableListOf(
                Player(
                    playerId = "1,",
                    name = "greg",
                    phoneNumber = "1",
                    email = "2",
                    contactId = 1,
                ),
            )
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testPlayersShown() {
        // Simple verification for now. As we are mostly making sure nothing crashes
        composeTestRule
            .onNodeWithText("greg")
            .assertIsDisplayed()
    }
}
