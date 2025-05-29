package com.gregmcgowan.fivesorganiser

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.rules.RuleChain

open class BaseUITest {

    private val composeTestRule: ComposeContentTestRule = createComposeRule()

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(composeTestRule)
            .around(DetectLeaksAfterTestSuccess())

    fun runComposeTest(composeContentTestRule: ComposeContentTestRule = composeTestRule,
                       testBody: ComposeContentTestRule.() -> Unit) {
        testBody.invoke(composeContentTestRule)
    }

}
