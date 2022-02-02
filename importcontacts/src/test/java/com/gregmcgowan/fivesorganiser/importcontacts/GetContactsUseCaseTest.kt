package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.JFixture
import com.google.common.truth.Truth.assertThat
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import com.gregmcgowan.fivesorganiser.data.player.Player
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import com.gregmcgowan.fivesorganiser.test_shared.createList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetContactsUseCaseTest {

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var fakePlayerRepo: FakePlayerRepo
    private lateinit var fakeContactImporter: FakeContactsImporter

    private lateinit var fixture: JFixture

    private lateinit var sut: GetContactsUseCaseImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        fakePlayerRepo = FakePlayerRepo()
        fakeContactImporter = FakeContactsImporter()

        sut = GetContactsUseCaseImpl(
                fakePlayerRepo,
                fakeContactImporter,
                CoroutineDispatchers(
                        coroutinesTestRule.testDispatcher,
                        coroutinesTestRule.testDispatcher
                )
        )
    }

    @Test
    fun `get contacts when none are already added`() = runTest {
        val fixtContacts: List<Contact> = fixture.createList()
        fakePlayerRepo.players = mutableListOf()
        fakeContactImporter.contacts = fixtContacts

        val output = sut.execute()
        assertThat(output).containsAllIn(fixtContacts)
    }

    @Test
    fun `get contacts when player is added already`() = runTest {
        val fixtContacts: List<Contact> = fixture.createList()

        val fixtPlayerAdded = Player(
                playerId = fixture.build(),
                contactId = fixtContacts[1].contactId,
                name = fixture.build(),
                phoneNumber = fixture.build(),
                email = fixture.build()
        )

        fakePlayerRepo.players = mutableListOf(fixtPlayerAdded)
        fakeContactImporter.contacts = fixtContacts

        val output = sut.execute()
        assertThat(output).containsAllIn(arrayOf(fixtContacts[0], fixtContacts[2]))
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts throws when player repo throws`() = runTest {
        fakePlayerRepo.exception = fixture.create(RuntimeException::class.java)

        sut.execute()
    }

    @Test(expected = RuntimeException::class)
    fun `get contacts when contact importer throws`() = runTest {
        fakePlayerRepo.players = mutableListOf()
        fakeContactImporter.exception = fixture.create(RuntimeException::class.java)

        sut.execute()
    }


}