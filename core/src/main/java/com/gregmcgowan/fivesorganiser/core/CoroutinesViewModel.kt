package com.gregmcgowan.fivesorganiser.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

open class CoroutinesViewModel(
        private val coroutineDispatchers: CoroutineDispatchers
) : ViewModel() {

    fun <T> launch(backgroundBlock: suspend () -> T,
                   uiBlock: (T) -> Unit) {
        viewModelScope.launch {
            uiBlock(withContext(coroutineDispatchers.io) {
                backgroundBlock.invoke()
            }
            )
        }
    }


}