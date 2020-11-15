package com.gregmgowan.fivesorganiser.test_shared

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.gregmcgowan.fivesorganiser.core.ViewModelActivity
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

class TestApp : Application(), HasAndroidInjector {

    lateinit var activityInjector: AndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_NoActionBar)
    }

    override fun androidInjector(): AndroidInjector<Any> = activityInjector

}

fun injectViewModeProviderFactory(viewModelProvider: ViewModelProvider.Factory) {
    ApplicationProvider.getApplicationContext<TestApp>().activityInjector = AndroidInjector { instance ->
        instance as ViewModelActivity
        instance.viewHolderFactory = viewModelProvider
    }
}



