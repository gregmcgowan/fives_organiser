package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithText
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsScreen
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState

fun ComposeContentTestRule.givenContactListUiState(uiState: ImportContactsUiState) {
    this.setContent {
        AppTheme { ImportContactsScreen(uiState, {}, {}) }
    }
}

fun ComposeContentTestRule.assertContactListScreen(assertions: ContactListAssertions.() -> Unit) {
    ContactListAssertions(this).assertions()
}

class ContactListAssertions(private val composeTestRule: ComposeContentTestRule) {
    private val items: SemanticsNodeInteractionCollection
        get() =
            composeTestRule.onAllNodesWithContentDescription(
                label = "ContactItem",
                substring = true,
            )

    var size: Int by setOnly {
        items.assertCountEquals(it)
    }

    fun noContactsShown() {
        items.assertCountEquals(0)
    }

    fun item(
        index: Int,
        assertions: ContactListItemAssertions.() -> Unit,
    ) {
        ContactListItemAssertions(items[index]).assertions()
    }

    fun permissionDeniedUi(assertions: UserDeniedPermissionAssertions.() -> Unit) {
        UserDeniedPermissionAssertions(composeTestRule).assertions()
    }

    fun addButtonShown() {
        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
    }

    fun addButtonNotShown() {
        composeTestRule.onNodeWithText("Add").assertDoesNotExist()
    }
}

class UserDeniedPermissionAssertions(private val composeTestRule: ComposeContentTestRule) {
    fun messageShown() {
        composeTestRule
            .onNodeWithText(
                "We need contact permission to import from your contacts.\n" +
                    "It will be quicker than entering it manually.\n" +
                    "We won't use it for anything bad.. honest",
            )
            .assertIsDisplayed()
    }

    fun tryAgainButtonShown() {
        composeTestRule
            .onNodeWithText("I believe you, let's try again")
            .assertIsDisplayed()
    }

    fun dontTryAgainButtonShown() {
        composeTestRule
            .onNodeWithText("No I don't want to give you permission")
            .assertIsDisplayed()
    }
}

class ContactListItemAssertions(private val item: SemanticsNodeInteraction) {
    var name: String by setOnly {
        item.onChildAt(1).assertTextEquals(it)
    }

    fun selected() {
        checkbox().assertIsOn()
    }

    fun notSelected() {
        checkbox().assertIsOff()
    }

    private fun checkbox() = item.onChildAt(2)
}
