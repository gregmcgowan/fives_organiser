package com.gregmcgowan.fivesorgainser.playerlist

import TEST_COROUTINE_DISPATCHERS
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.data.DataChangeType
import com.gregmcgowan.fivesorganiser.data.DataUpdate
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmgowan.fivesorganiser.test_shared.getValueForTest
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.lang.RuntimeException

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class PlayerListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockUiModelMapper: PlayerListUiModelMapper

    @Mock
    lateinit var mockGetPlayerUpdatesUsecase: GetPlayerListUpdatesUseCase

    @Fixture
    lateinit var fixtPlayerDataUpdate: DataUpdate<Player>

    @Fixture
    lateinit var fixtPlayerUiModel: PlayerListUiModel

    private lateinit var fixture: JFixture

    private lateinit var sut: PlayerListViewModel

    private val testDispatchers = TEST_COROUTINE_DISPATCHERS

    @Before
    fun setUp() {
        fixture = JFixture()
        fixture.customise().useSubType(DataChangeType::class.java, DataChangeType.Added::class.java)
        fixture.customise().sameInstance(DataChangeType::class.java, DataChangeType.Added)
        FixtureAnnotations.initFixtures(this, fixture)
        MockitoAnnotations.initMocks(this)
    }


    @Test
    fun `init() loads player list when there are players to load`() {
        // check loading model
        val playerUpdatesLiveData = MutableLiveData<Either<Exception, DataUpdate<Player>>>()
        setupSut(playerUpdatesLiveData)

        val loadingOutput = sut.playerUiModelLiveData.getValueForTest()
        assertThat(loadingOutput, equalTo(PlayerListUiModel(emptyList(), false, true, false, NO_STRING_RES_ID)))

        // setup first ui model
        whenever(mockUiModelMapper.map(loadingOutput, fixtPlayerDataUpdate)).thenReturn(fixtPlayerUiModel)
        playerUpdatesLiveData.postValue(Either.Right(fixtPlayerDataUpdate))

        val uiUpdates = sut.playerUiModelLiveData.getValueForTest()
        assertThat(uiUpdates, equalTo(fixtPlayerUiModel))
    }


    @Test
    fun `live data sends data after initial load`() {
        // check loading model
        val playerUpdatesLiveData = MutableLiveData<Either<Exception, DataUpdate<Player>>>()
        setupSut(playerUpdatesLiveData)

        val loadingOutput = sut.playerUiModelLiveData.getValueForTest()
        assertThat(loadingOutput, equalTo(PlayerListUiModel(emptyList(), false, true, false, NO_STRING_RES_ID)))

        // set up first ui model
        whenever(mockUiModelMapper.map(loadingOutput, fixtPlayerDataUpdate)).thenReturn(fixtPlayerUiModel)
        playerUpdatesLiveData.postValue(Either.Right(fixtPlayerDataUpdate))
        val uiUpdatesOutput = sut.playerUiModelLiveData.getValueForTest()

        assertThat(uiUpdatesOutput, equalTo(fixtPlayerUiModel))

        // setup new ui model
        val fixtNewUiModel = fixture.create(PlayerListUiModel::class.java)
        whenever(mockUiModelMapper.map(uiUpdatesOutput, fixtPlayerDataUpdate))
                .thenReturn(fixtNewUiModel)
        // post another update
        playerUpdatesLiveData.postValue(Either.Right(fixtPlayerDataUpdate))

        val updatedOutput = sut.playerUiModelLiveData.getValueForTest()
        assertThat(updatedOutput, equalTo(fixtNewUiModel))
    }


    @Test
    fun `init() displays error message when initial loading fails`() {
        // check loading model
        val playerUpdatesLiveData = MutableLiveData<Either<Exception, DataUpdate<Player>>>()
        setupSut(playerUpdatesLiveData)

        val expectedLoadingModel = PlayerListUiModel(emptyList(), false, true, false, NO_STRING_RES_ID)
        val loadingOutput = sut.playerUiModelLiveData.getValueForTest()

        assertThat(loadingOutput, equalTo(expectedLoadingModel))

        // setup and verifying error
        playerUpdatesLiveData.postValue(Either.Left(RuntimeException()))

        val errorModel = sut.playerUiModelLiveData.getValueForTest()

        assertThat(errorModel, equalTo(PlayerListUiModel(
                players = emptyList(),
                showLoading = true,
                showErrorMessage = true,
                showPlayers = false,
                errorMessage = R.string.generic_error_message
        )))
    }

    @Test
    fun `add player sends add player nav event`() {
        // check loading model
        val playerUpdatesLiveData = MutableLiveData<Either<Exception, DataUpdate<Player>>>()
        setupSut(playerUpdatesLiveData)

        sut.addPlayerButtonPressed()

        val output = sut.playerListUiLiveData.getValueForTest()

        assertThat(output as PlayerListUiEvents.ShowAddPlayerScreenEvent,
                equalTo(PlayerListUiEvents.ShowAddPlayerScreenEvent))
    }

    private fun setupSut(playerUpdatesLiveData: MutableLiveData<Either<Exception, DataUpdate<Player>>>) {
        whenever(mockGetPlayerUpdatesUsecase.execute()).thenReturn(playerUpdatesLiveData)

        sut = PlayerListViewModel(
                mockUiModelMapper,
                mockGetPlayerUpdatesUsecase,
                testDispatchers
        )
    }
}