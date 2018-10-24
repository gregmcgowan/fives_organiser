package com.gregmgowan.fivesorganiser.test_shared

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.gregmcgowan.fivesorganiser.core.ViewModelActivity
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector

class TestApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    lateinit var activityInjector: AndroidInjector<Activity>
    lateinit var fragmentInject: AndroidInjector<Fragment>

    override fun onCreate() {
        super.onCreate()
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_NoActionBar)
    }

    override fun activityInjector() = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInject

}

fun injectViewModeProviderFactory(viewModelProvider: ViewModelProvider.Factory) {
    ApplicationProvider.getApplicationContext<TestApp>().activityInjector = AndroidInjector { instance ->
        instance as ViewModelActivity
        instance.viewHolderFactory = viewModelProvider
    }
}



