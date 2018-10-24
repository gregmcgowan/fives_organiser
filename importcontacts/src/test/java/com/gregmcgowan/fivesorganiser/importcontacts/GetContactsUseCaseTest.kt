package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.google.common.truth.Truth.assertThat
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.lang.RuntimeException

class GetContactsUseCaseTest {

    @Mock
    lateinit var mockPlayerRepo: PlayerRepo

    @Mock
    lateinit var mockContactsImporter: ContactImporter

    @Fixture
    lateinit var fixtContacts: List<Contact>

    private lateinit var fixture: JFixture

    private lateinit var sut: GetContactsUseCase

    @Before
    fun setUp() {
        fixture = JFixture()
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this, fixture)

        sut = GetContactsUseCase(
                mockPlayerRepo,
                mockContactsImporter
        )
    }

    @Test
    fun `get contacts when none are already added`() = runBlocking {
        whenever(mockPlayerRepo.getPlayers()).thenReturn(emptyList())
        whenever(mockContactsImporter.getAllContacts()).thenReturn(fixtContacts)

        val output = sut.execute()

        assertThat(output).containsAllIn(fixtContacts)
        Unit
    }

    @Test
    fun `get contacts when player is added already`() = runBlocking {
        // TODO use set final
        val fixtPlayerAdded = Player(
                playerId = fixture.create(String::class.java),
                contactId = fixtContacts[1].contactId,
                name = fixture.create(String::class.java),
                phoneNumber = fixture.create(String::class.java),
                email = fixture.create(String::class.java)
        )

        whenever(mockPlayerRepo.getPlayers()).thenReturn(listOf(fixtPlayerAdded))
        whenever(mockContactsImporter.getAllContacts()).thenReturn(fixtContacts)

        val output = sut.execute()

        assertThat(output).containsAllIn(arrayOf(fixtContacts[0], fixtContacts[2]))

        Unit
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts throws when player repo throws`() = runBlocking {
        whenever(mockPlayerRepo.getPlayers())
                .thenThrow(fixture.create(RuntimeException::class.java))

        sut.execute()

        Unit
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts when contact importer throws`() = runBlocking {
        whenever(mockPlayerRepo.getPlayers()).thenReturn(emptyList())
        whenever(mockContactsImporter.getAllContacts())
                .thenThrow(fixture.create(RuntimeException::class.java))

        sut.execute()

        Unit
    }
}