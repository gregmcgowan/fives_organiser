package com.gregmcgowan.fivesorganiser.data.match

import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MatchInteractorTest {

    @Mock
    lateinit var mockMatchRepo: MatchRepo
    @Mock
    lateinit var mockSquadRepo: MatchSquadRepo
    @Mock
    lateinit var mockPlayerRepo: PlayerRepo

    @Mock
    lateinit var mockMatchTypeHelper: MatchTypeHelper

    lateinit var sut: MatchInteractor

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        sut = MatchInteractor(mockMatchRepo, mockSquadRepo, mockPlayerRepo)
    }


    @Test
    fun init() {
        assertNotNull(sut)
    }
}