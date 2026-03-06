package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import com.gregmcgowan.fivesorganiser.test_shared.createList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerListViewModelTest {
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)

    private lateinit var fixture: JFixture
    private lateinit var sut: PlayerListViewModel

    @Before
    fun setUp() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)
    }

    @Test
    fun `init() loads player list when there are players to load`() =
        runTest {
            val fakePlayerListUseCase = FakeGetPlayerListUseCase()
            val fakePlayerListUiModelMapper = FakePlayerListUiStateMapperImpl()
            sut =
                PlayerListViewModel(
                    uiStateMapper = fakePlayerListUiModelMapper,
                    getPlayerListUseCase = fakePlayerListUseCase,
                )
            runCurrent()

            assertThat(sut.uiStateFlow.value, instanceOf(LoadingUiState::class.java))

            // setup first ui state
            val expected = ContentUiState(fixture.build<PlayerListUiState>())
            fakePlayerListUiModelMapper.state = expected
            fakePlayerListUseCase.players = fixture.createList<Player>().toMutableList()
            sut.init()

            runCurrent()

            assertThat(sut.uiStateFlow.value, equalTo(expected))
        }

    @Test
    fun `init() displays error message when initial loading fails`() =
        runTest {
            val fakePlayerListUseCase = FakeGetPlayerListUseCase()
            sut =
                PlayerListViewModel(
                    uiStateMapper = FakePlayerListUiStateMapperImpl(),
                    getPlayerListUseCase = fakePlayerListUseCase,
                )

            assertThat(sut.uiStateFlow.value, instanceOf(LoadingUiState::class.java))

            // setup and verifying error
            fakePlayerListUseCase.exception = RuntimeException()
            sut.init()
            runCurrent()

            assertThat(sut.uiStateFlow.value, instanceOf(ErrorUiState::class.java))
        }

    // TODO create module for common fakes
    class FakeGetPlayerListUseCase : GetPlayerListUseCase {
        var players: MutableList<Player> = mutableListOf()

        var exception: RuntimeException? = null

        override suspend fun execute(): List<Player> {
            if (exception != null) {
                throw exception!!
            } else {
                return players
            }
        }
    }

    private class FakePlayerListUiStateMapperImpl : PlayerListUiStateMapper {
        lateinit var state: UiState<PlayerListUiState>

        override fun map(players: List<Player>): UiState<PlayerListUiState> = state
    }
}
