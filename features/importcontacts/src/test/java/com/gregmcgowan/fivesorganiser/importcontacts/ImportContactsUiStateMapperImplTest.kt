package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.test_shared.createList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test

class ImportContactsUiStateMapperImplTest {
    private lateinit var fixture: JFixture

    private lateinit var sut: ImportContactsUiStateMapperImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        sut = ImportContactsUiStateMapperImpl()
    }

    @Test
    fun `map() when there are contacts and no selected contacts`() {
        val fixtContacts: List<Contact> = fixture.createList()
        val output = sut.map(fixtContacts, emptySet())

        assertThat(output.addContactsButtonEnabled, equalTo(false))
        assertThat(output.contacts, hasSize(3))
        assertContact(fixtContacts[0], false, output.contacts[0])
        assertContact(fixtContacts[1], false, output.contacts[1])
        assertContact(fixtContacts[2], false, output.contacts[2])
    }

    @Test
    fun `map() when there are contacts and selected contacts`() {
        val fixtContacts: List<Contact> = fixture.createList()
        val output = sut.map(fixtContacts, setOf(fixtContacts[1].contactId))

        assertThat(output.addContactsButtonEnabled, equalTo(true))
        assertThat(output.contacts, hasSize(3))
        assertContact(fixtContacts[0], false, output.contacts[0])
        assertContact(fixtContacts[1], true, output.contacts[1])
        assertContact(fixtContacts[2], false, output.contacts[2])
    }

    @Test
    fun `map() when there are no contacts`() {
        val output = sut.map(emptyList(), emptySet())

        assertThat(output.addContactsButtonEnabled, equalTo(false))
        assertThat(output.contacts, hasSize(0))
    }

    private fun assertContact(
        contact: Contact,
        isSelected: Boolean,
        actual: ContactItemUiState,
    ) {
        val expectedUiState = ContactItemUiState(contact.name, isSelected, contact.contactId)
        assertThat(actual, equalTo(expectedUiState))
    }
}
