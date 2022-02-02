package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.createList
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SavePlayersUseCaseTest {

    @get:Rule var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var fixture: JFixture
    private lateinit var fakePlayerRepo: FakePlayerRepo
    private lateinit var fakeContactImporter: FakeContactsImporter

    private lateinit var sut: SavePlayersUseCaseImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        fakePlayerRepo = FakePlayerRepo()
        fakeContactImporter = FakeContactsImporter()

        sut = SavePlayersUseCaseImpl(
                fakePlayerRepo,
                fakeContactImporter,
                CoroutineDispatchers(
                        coroutinesTestRule.testDispatcher,
                        coroutinesTestRule.testDispatcher
                )
        )
    }

    @Test
    fun `save selected contacts`() = runTest {
        val fixtContacts: List<Contact> = fixture.createList()
        fakeContactImporter.contacts = fixtContacts
        val fixtContact = fixtContacts[1]

        sut.execute(setOf(fixtContact.contactId))

        assertThat(fakePlayerRepo.players[0],
                equalTo(Player(
                        playerId = "0",
                        name = fixtContact.name,
                        phoneNumber = fixtContact.phoneNumber,
                        email = fixtContact.emailAddress,
                        contactId = fixtContact.contactId))
        )
    }

    @Test
    fun `do save when there is no selected contacts`() = runTest {
        val fixtContacts: List<Contact> = fixture.createList()
        fakeContactImporter.contacts = fixtContacts

        sut.execute(emptySet())

        assertThat(fakePlayerRepo.players, hasSize(0))
    }
}

