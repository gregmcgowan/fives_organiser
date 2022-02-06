package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.test_shared.createList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test

class ImportContactsUiModelMapperImplTest {

    private lateinit var fixture: JFixture

    private lateinit var sut: ImportContactsUiModelMapperImpl

    @Before
    fun setUp() {
        fixture = JFixture()
        sut = ImportContactsUiModelMapperImpl()
    }


    @Test
    fun `map() when there are contacts and no selected contacts`() {
        val fixtContacts: List<Contact> = fixture.createList()
        val output = sut.map(fixtContacts, emptySet())

        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showContent, equalTo(true))
        assertThat(output.importContactsButtonEnabled, equalTo(false))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.contacts, hasSize(3))
        assertContact(fixtContacts[0], false, output.contacts[0])
        assertContact(fixtContacts[1], false, output.contacts[1])
        assertContact(fixtContacts[2], false, output.contacts[2])
    }


    @Test
    fun `map() when there are contacts and selected contacts`() {
        val fixtContacts: List<Contact> = fixture.createList()
        val output = sut.map(fixtContacts, setOf(fixtContacts[1].contactId))

        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showContent, equalTo(true))
        assertThat(output.importContactsButtonEnabled, equalTo(true))
        assertThat(output.errorMessage, equalTo(NO_STRING_RES_ID))
        assertThat(output.contacts, hasSize(3))
        assertContact(fixtContacts[0], false, output.contacts[0])
        assertContact(fixtContacts[1], true, output.contacts[1])
        assertContact(fixtContacts[2], false, output.contacts[2])
    }

    @Test
    fun `map() when there are no contacts`() {
        val output = sut.map(emptyList(), emptySet())

        assertThat(output.showLoading, equalTo(false))
        assertThat(output.showContent, equalTo(false))
        assertThat(output.importContactsButtonEnabled, equalTo(false))
        assertThat(output.errorMessage, equalTo(R.string.no_contacts_message))
        assertThat(output.contacts, hasSize(0))

    }

    private fun assertContact(contact: Contact,
                              isSelected: Boolean,
                              actual: ContactItemUiModel) {
        val expectedUiModel = ContactItemUiModel(contact.name, isSelected, contact.contactId)
        assertThat(actual, equalTo(expectedUiModel))
    }
}
