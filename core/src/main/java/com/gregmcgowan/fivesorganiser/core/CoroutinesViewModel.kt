package com.gregmcgowan.fivesorganiser.core

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*

open class CoroutinesViewModel(private val dispatchers: Dispatchers) : ViewModel() {

    private val parent: Job = Job()

    private val uiScope = CoroutineScope(dispatchers.ui + parent)

    fun <T> launch(backgroundBlock: suspend () -> T,
                   uiBlock: (T) -> Unit) {
        uiScope.launch {
            if (isActive) {
                uiBlock(async(dispatchers.io) { backgroundBlock.invoke() }.await())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parent.cancel()
    }

}