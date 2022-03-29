package com.gregmcgowan.fivesorganiser

import com.gregmcgowan.fivesorganiser.importcontacts.ContactItemUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import org.junit.Test

class ImportContactsUITest : BaseUITest(){


    @Test
    fun showListOfContactsWithOneSelected() {
        composeTestRule.givenContactListUiState(ContactsListUiState(
                listOf(
                        ContactItemUiState(name = "Greg", isSelected = false, contactId = 1),
                        ContactItemUiState(name = "Frances", isSelected = false, contactId = 2),
                        ContactItemUiState(name = "Joe Wicks", isSelected = true, contactId = 3)
                ),
                addContactsButtonEnabled = true,
        ))


        composeTestRule.assertContactListScreen {
            size = 3
            item(0) {
                name = "Greg"
                notSelected()
            }
            item(1) {
                name = "Frances"
                notSelected()
            }
            item(2) {
                name = "Joe Wicks"
                selected()
            }
            addButtonShown()
        }
    }

    @Test
    fun showListOfContactsWithNoneSelected() {
        composeTestRule.givenContactListUiState(ContactsListUiState(
                listOf(
                        ContactItemUiState(name = "Greg", isSelected = false, contactId = 1),
                        ContactItemUiState(name = "Frances", isSelected = false, contactId = 2),
                        ContactItemUiState(name = "Joe Wicks", isSelected = false, contactId = 3)
                ),
                addContactsButtonEnabled = false,
        ))


        composeTestRule.assertContactListScreen {
            size = 3
            item(0) {
                name = "Greg"
                notSelected()
            }
            item(1) {
                name = "Frances"
                notSelected()
            }
            item(2) {
                name = "Joe Wicks"
                notSelected()
            }
            addButtonNotShown()
        }
    }

}

