package com.gregmgowan.fivesorganiser.test_shared

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat

class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }

        val recyclerView = view as RecyclerView
        with(recyclerView.adapter){
            requireNotNull(this)
            assertThat(this.itemCount, `is`(expectedCount))
        }
    }
}