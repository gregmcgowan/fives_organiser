package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.main.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerListIntegrationTest {
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // Bind value so we can set player data in the fake
    /*  @BindValue
      @JvmField
      val repo: PlayerRepo =
          FakePlayerRepo()
              .apply {
                  this.players =
                      mutableListOf(
                          Player("1,", "greg", "1", "2", 1),
                      )
              }
     */
    @Before
    fun setup() {
        val fivesOrganiserApp = ApplicationProvider.getApplicationContext<FivesOrganiserApp>()
        FakePlayerRepoModule.fakePlayerRepo.players =
            mutableListOf(
                Player(
                    playerId = "1,",
                    name = "greg",
                    phoneNumber = "1",
                    email = "2",
                    contactId = 1
                ),
            )
    }

    @Test
    fun testPlayersShown() {
        // Simple verification for now. As we are mostly making sure nothing crashes
        composeTestRule
            .onNodeWithText("greg")
            .assertIsDisplayed()
    }
}
