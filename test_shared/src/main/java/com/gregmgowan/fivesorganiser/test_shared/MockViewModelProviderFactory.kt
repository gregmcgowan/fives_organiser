package com.gregmgowan.fivesorganiser.test_shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.mockito.Mockito

 abstract class MockViewModelProviderFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return Mockito.mock(modelClass).apply { init(this) }
    }

    abstract fun <T : ViewModel?> init(viewModel: T?)
}