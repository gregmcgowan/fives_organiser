package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.*
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PlayerListViewModelTest {

    @get:Rule var coroutinesTestRule = CoroutinesTestRule()

    @Mock lateinit var mockUiModelMapper: PlayerListUiModelMapper

    private val testCoroutineDispatcher get() = coroutinesTestRule.testDispatcher
    private lateinit var fixture: JFixture
    private lateinit var sut: PlayerListViewModel

    @Before
    fun setUp() {
        fixture = JFixture()
        fixture.customise().useSubType(DataChangeType::class.java, DataChangeType.Added::class.java)
        fixture.customise().sameInstance(DataChangeType::class.java, DataChangeType.Added)

        FixtureAnnotations.initFixtures(this, fixture)
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun `init() loads player list when there are players to load`() = testCoroutineDispatcher.runBlockingTest {
        val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
        sut = PlayerListViewModel(mockUiModelMapper, fakeGetPlayersUseCase)

        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))

        // setup first ui model
        val expected = ContentUiModel(fixture.create(PlayerListUiModel::class.java))
        pauseAndDispatch(fakeGetPlayersUseCase, fixtPlayerDataUpdate, expected)

        assertThat(sut.uiModel, equalTo(expected))
    }


    @Test
    fun `ui model updates data after initial load`() = testCoroutineDispatcher.runBlockingTest {
        val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
        sut = PlayerListViewModel(mockUiModelMapper, fakeGetPlayersUseCase)

        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))

        // set up first ui model
        val expected = ContentUiModel(fixture.create(PlayerListUiModel::class.java))
        pauseAndDispatch(fakeGetPlayersUseCase, fixtPlayerDataUpdate, expected)

        assertThat(sut.uiModel, equalTo(expected))


        // setup new ui model
        val fixtNewUpdate: DataUpdate<Player> = fixDataUpdate()
        val fixtNewUiModel = ContentUiModel(fixture.create(PlayerListUiModel::class.java))
        pauseAndDispatch(fakeGetPlayersUseCase, fixtNewUpdate, fixtNewUiModel)

        assertThat(sut.uiModel, equalTo(fixtNewUiModel))
    }


    @Test
    fun `init() displays error message when initial loading fails`() = testCoroutineDispatcher.runBlockingTest {
        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
        sut = PlayerListViewModel(mockUiModelMapper, fakeGetPlayersUseCase)

        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))

        // setup and verifying error
        testCoroutineDispatcher.pauseDispatcher()
        fakeGetPlayersUseCase.emit(Either.Left(RuntimeException()))
        testCoroutineDispatcher.resumeDispatcher()

        assertThat(sut.uiModel, instanceOf(ErrorUiModel::class.java))
    }

    @Test
    fun `add player sends add player nav event`() = testCoroutineDispatcher.runBlockingTest {
//        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
//        sut = PlayerListViewModel(mockUiModelMapper, fakeGetPlayersUseCase)
//
//        testCoroutineDispatcher.pauseDispatcher()
//        sut.addPlayerButtonPressed()
//
//        val output = sut.playerListUiEvents.first()
//        testCoroutineDispatcher.resumeDispatcher()
//
//        assertThat(output as ShowAddPlayerScreenEvent, equalTo(ShowAddPlayerScreenEvent))
    }

    private fun fixDataUpdate() =
            DataUpdate(
                    listOf(DataChange<Player>(
                            fixture.create(DataChangeType::class.java),
                            fixture.create(Player::class.java)
                    ))
            )

    private suspend fun pauseAndDispatch(useCase: FakeGetPlayersUseCase,
                                         dataUpdate: DataUpdate<Player>,
                                         expected: ContentUiModel<PlayerListUiModel>) {
        testCoroutineDispatcher.pauseDispatcher()
        whenever(mockUiModelMapper.map(sut.uiModel, dataUpdate)).thenReturn(expected)
        useCase.emit(Either.Right(dataUpdate))
        testCoroutineDispatcher.resumeDispatcher()
    }

    private class FakeGetPlayersUseCase : GetPlayerListUpdatesUseCase {

        private val flow = MutableSharedFlow<Either<Throwable, DataUpdate<Player>>>()

        suspend fun emit(either: Either<Throwable, DataUpdate<Player>>) {
            flow.emit(either)
        }

        override suspend fun execute(): Flow<Either<Throwable, DataUpdate<Player>>> {
            return flow
        }

    }

}