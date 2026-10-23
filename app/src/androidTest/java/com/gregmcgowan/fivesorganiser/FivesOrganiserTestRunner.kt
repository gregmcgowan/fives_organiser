package com.gregmcgowan.fivesorganiser

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class FivesOrganiserTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        name: String?,
        context: Context?,
    ): Application {
        return super.newApplication(cl, FivesOrganiserApp::class.java.name, context)
    }
}
