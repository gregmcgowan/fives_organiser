package com.gregmcgowan.fivesorganiser.matchlist

import TEST_COROUTINE_DISPTACHERS_AND_CONTEXT
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MatchListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockMatchUpdatesUseCase: GetMatchUpdatesUseCase

    @Mock
    lateinit var mockMapper: MatchListUiModelMappers


    private lateinit var sut: MatchListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        sut = MatchListViewModel(
                mockMatchUpdatesUseCase,
                mockMapper,
                TEST_COROUTINE_DISPTACHERS_AND_CONTEXT
        )
    }


}