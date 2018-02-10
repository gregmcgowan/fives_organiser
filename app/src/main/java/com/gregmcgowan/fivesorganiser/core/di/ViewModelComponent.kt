package com.gregmcgowan.fivesorganiser.core.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Provider

interface ViewModelComponent {

    fun viewModelFactory(): ViewModelProvider.Factory

    fun map(): Map<Class<out ViewModel>, Provider<ViewModel>>

}