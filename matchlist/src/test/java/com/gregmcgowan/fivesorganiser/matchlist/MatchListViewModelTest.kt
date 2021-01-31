package com.gregmcgowan.fivesorganiser.matchlist

import TEST_COROUTINE_DISPATCHERS
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gregmcgowan.fivesorganiser.data.match.MatchInteractor
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MatchListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockMatchInteractor: MatchInteractor

    @Mock
    lateinit var mockMapper: MatchListUiModelMappers


    private lateinit var sut: MatchListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        sut = MatchListViewModel(
                mockMatchInteractor,
                mockMapper,
                TEST_COROUTINE_DISPATCHERS
        )
    }

    @Test
    fun onViewShown() {
        sut.onViewShown()

        val output = sut.matchListUiModelLiveData.value
        assertNotNull(output)
    }

}