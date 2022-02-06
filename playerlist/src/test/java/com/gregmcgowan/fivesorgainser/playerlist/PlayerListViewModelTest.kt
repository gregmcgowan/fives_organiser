package com.gregmcgowan.fivesorgainser.playerlist

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.ui.UiModel
import com.gregmcgowan.fivesorganiser.core.ui.UiModel.*
import com.gregmcgowan.fivesorganiser.data.DataChange
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
    fun `init() loads player list when there are players to load`() = runTest {
        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
        val fakePlayerListUiModelMapper = FakePlayerListUiModelMapperImpl()
        sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
        runCurrent()

        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))

        // setup first ui model
        val expected = ContentUiModel(fixture.build<PlayerListUiModel>())
        fakePlayerListUiModelMapper.model = expected
        val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
        fakeGetPlayersUseCase.emit(fixtPlayerDataUpdate)

        runCurrent()

        assertThat(sut.uiModel, equalTo(expected))
    }


    @Test
    fun `ui model updates data after initial load`() = runTest {
        val fakeGetPlayersUseCase = FakeGetPlayersUseCase()
        val fakePlayerListUiModelMapper = FakePlayerListUiModelMapperImpl()
        sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))
        runCurrent()

        // set up first ui model
        val expected = ContentUiModel(fixture.build<PlayerListUiModel>())
        fakePlayerListUiModelMapper.model = expected
        val fixtPlayerDataUpdate: DataUpdate<Player> = fixDataUpdate()
        fakeGetPlayersUseCase.emit(fixtPlayerDataUpdate)

        runCurrent()

        assertThat(sut.uiModel, equalTo(expected))

        // setup new ui model
        val fixtNewUpdate: DataUpdate<Player> = fixDataUpdate()
        val fixtNewUiModel = ContentUiModel(fixture.build<PlayerListUiModel>())
        fakePlayerListUiModelMapper.model = fixtNewUiModel
        fakeGetPlayersUseCase.emit(fixtNewUpdate)

        runCurrent()

        assertThat(sut.uiModel, equalTo(fixtNewUiModel))
    }


    @Test
    fun `init() displays error message when initial loading fails`() = runTest {
        val fakeGetPlayersUseCase = FakeGetPlayersUseCaseWithException()
        val fakePlayerListUiModelMapper = FakePlayerListUiModelMapperImpl()
        sut = PlayerListViewModel(fakePlayerListUiModelMapper, fakeGetPlayersUseCase)
        assertThat(sut.uiModel, instanceOf(LoadingUiModel::class.java))

        // setup and verifying error
        fakeGetPlayersUseCase.exception = RuntimeException()
        runCurrent()

        assertThat(sut.uiModel, instanceOf(ErrorUiModel::class.java))
    }

    private fun fixDataUpdate() =
            DataUpdate(listOf(DataChange<Player>(fixture.build(), fixture.build())))

    private class FakeGetPlayersUseCase : GetPlayerListUpdatesUseCase {

        val flow = MutableSharedFlow<DataUpdate<Player>>()

        suspend fun emit(either: DataUpdate<Player>) {
            flow.emit(either)
        }

        override suspend fun execute(): Flow<DataUpdate<Player>> {
            return flow
        }

    }

    private class FakeGetPlayersUseCaseWithException : GetPlayerListUpdatesUseCase {

        lateinit var exception: RuntimeException

        override suspend fun execute(): Flow<DataUpdate<Player>> {
            throw exception
        }

    }

    private class FakePlayerListUiModelMapperImpl : PlayerListUiModelMapper {

        lateinit var model: UiModel<PlayerListUiModel>

        override fun map(existingModel: UiModel<PlayerListUiModel>,
                         updates: DataUpdate<Player>): UiModel<PlayerListUiModel> {
            return model
        }

    }

}
