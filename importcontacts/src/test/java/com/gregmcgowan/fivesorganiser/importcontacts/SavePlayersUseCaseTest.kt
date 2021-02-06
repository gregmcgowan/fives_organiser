package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.gregmgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SavePlayersUseCaseTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var mockPlayerRepo: PlayerRepo

    @Mock
    lateinit var mockContactsImporter: ContactImporter

    @Fixture
    lateinit var fixtContacts: List<Contact>

    private lateinit var fixture: JFixture

    private lateinit var sut: SavePlayersUseCase

    private val mainDispatcher = TestCoroutineDispatcher()

    private val ioDispatcher: TestCoroutineDispatcher
        get() = coroutinesTestRule.testDispatcher

    @Before
    fun setUp() {
        fixture = JFixture()
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this, fixture)

        sut = SavePlayersUseCase(
                mockPlayerRepo,
                mockContactsImporter,
                CoroutineDispatchers(
                        mainDispatcher,
                        ioDispatcher
                )
        )
    }

    @Test
    fun `save selected contacts`() = ioDispatcher.runBlockingTest {
        whenever(mockContactsImporter.getAllContacts()).thenReturn(fixtContacts)
        val fixtContact = fixtContacts[1]

        val output = sut.execute(setOf(fixtContact.contactId))

        assert(output.isRight)
        verify(mockPlayerRepo).addPlayer(
                fixtContact.name,
                fixtContact.emailAddress,
                fixtContact.phoneNumber,
                fixtContact.contactId
        )
        verifyNoMoreInteractions(mockPlayerRepo)
    }

    @Test
    fun `do save when there is no selected contacts`() = ioDispatcher.runBlockingTest {
        whenever(mockContactsImporter.getAllContacts()).thenReturn(fixtContacts)

        val output = sut.execute(emptySet())

        assert(output.isRight)
        verifyZeroInteractions(mockPlayerRepo)
    }
}

