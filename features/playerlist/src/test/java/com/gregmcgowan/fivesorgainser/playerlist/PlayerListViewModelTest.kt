package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ContentUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.core.ui.UiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
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
        fixture.customise().useSubType(DataChangeType::class.java, DataChangeType.Added::class.java)
        fixture.customise().sameInstance(DataChangeType::class.java, DataChangeType.Added)

        FixtureAnnotations.initFixtures(this, fixture)
    }

    @Test
    fun `init() loads player list when there are players to load`() =
        runTest {
            val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
            val fakePlayerListUiModelMapper = FakePlayerListUiStateMapperImpl()
            sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
            runCurrent()

            assertThat(sut.uiStateFlow.value, instanceOf(LoadingUiState::class.java))

            // setup first ui state
            val expected = ContentUiState(fixture.build<PlayerListUiState>())
            fakePlayerListUiModelMapper.state = expected
            val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
            fakeGetPlayersUseCase.emit(fixtPlayerDataUpdate)

            runCurrent()

            assertThat(sut.uiStateFlow.value, equalTo(expected))
        }

    @Test
    fun `ui model updates data after initial load`() =
        runTest {
            val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
            val fakePlayerListUiModelMapper = FakePlayerListUiStateMapperImpl()
            sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
            assertThat(sut.uiStateFlow.value, instanceOf(LoadingUiState::class.java))
            runCurrent()

            // set up first ui state
            val expected = ContentUiState(fixture.build<PlayerListUiState>())
            fakePlayerListUiModelMapper.state = expected
            val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
            fakeGetPlayersUseCase.emit(fixtPlayerDataUpdate)

            runCurrent()

            assertThat(sut.uiStateFlow.value, equalTo(expected))

            // setup new ui state
            val fixtNewUpdate: DataUpdate<Player> = fixDataUpdate()
            val fixtNewUiModel = ContentUiState(fixture.build<PlayerListUiState>())
            fakePlayerListUiModelMapper.state = fixtNewUiModel
            fakeGetPlayersUseCase.emit(fixtNewUpdate)

            runCurrent()

            assertThat(sut.uiStateFlow.value, equalTo(fixtNewUiModel))
        }

    @Test
    fun `init() displays error message when initial loading fails`() =
        runTest {
            val fakeGetPlayersUseCase = FakeGetPlayersUseCaseWithException()
            val fakePlayerListUiModelMapper = FakePlayerListUiStateMapperImpl()
            sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
            assertThat(sut.uiStateFlow.value, instanceOf(LoadingUiState::class.java))
            runCurrent()

            // setup and verifying error
            fakeGetPlayersUseCase.exception = RuntimeException()
            runCurrent()

            assertThat(sut.uiStateFlow.value, instanceOf(ErrorUiState::class.java))
        }

    private fun fixDataUpdate() = DataUpdate(listOf(DataChange<Player>(fixture.build(), fixture.build())))

    private class FakeGetPlayersUseCase : GetPlayerListUpdatesUseCase {
        val flow = MutableSharedFlow<DataUpdate<Player>>()

        suspend fun emit(either: DataUpdate<Player>) {
            flow.emit(either)
        }

        override fun execute(): Flow<DataUpdate<Player>> {
            return flow
        }
    }

    private class FakeGetPlayersUseCaseWithException : GetPlayerListUpdatesUseCase {
        lateinit var exception: RuntimeException

        override fun execute(): Flow<DataUpdate<Player>> =
            flow {
                throw exception
            }
    }

    private class FakePlayerListUiStateMapperImpl : PlayerListUiStateMapper {
        lateinit var state: UiState<PlayerListUiState>

        override fun map(
            existingState: UiState<PlayerListUiState>,
            updates: DataUpdate<Player>,
        ): UiState<PlayerListUiState> {
            return state
        }
    }
}
