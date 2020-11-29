package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.google.common.truth.Truth.assertThat
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepo
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import runBlockingUnit
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
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
    fun `get contacts when none are already added`() = runBlockingTest {
        whenever(mockPlayerRepo.getPlayers()).thenReturn(emptyList())
        whenever(mockContactsImporter.getAllContacts()).thenReturn(fixtContacts)

        sut.execute().either(
                { fail() },
                { contacts -> assertThat(contacts).containsAllIn(fixtContacts) }
        )
    }

    @Test
    fun `get contacts when player is added already`() = runBlockingTest {
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

        sut.execute().either(
                { fail() },
                { contacts ->
                    assertThat(contacts).containsAllIn(
                            arrayOf(fixtContacts[0], fixtContacts[2])
                    )
                }
        )
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts throws when player repo throws`() = runBlockingUnit {
        whenever(mockPlayerRepo.getPlayers())
                .thenThrow(fixture.create(RuntimeException::class.java))

        sut.execute()
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts when contact importer throws`() = runBlockingUnit {
        whenever(mockPlayerRepo.getPlayers()).thenReturn(emptyList())
        whenever(mockContactsImporter.getAllContacts())
                .thenThrow(fixture.create(RuntimeException::class.java))

        sut.execute()
    }
}