package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

open class BaseUITest {

    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()


    fun runComposeTest(composeContentTestRule: ComposeContentTestRule = composeTestRule,
                       testBody: ComposeContentTestRule.() -> Unit) {
        testBody.invoke(composeContentTestRule)
    }

}
