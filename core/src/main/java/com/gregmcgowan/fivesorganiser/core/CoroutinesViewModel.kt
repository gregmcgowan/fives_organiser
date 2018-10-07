package com.gregmcgowan.fivesorganiser.core

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

open class CoroutinesViewModel(private val coroutineContexts: CoroutineContexts) : ViewModel() {

    private val parent: Job = Job()

    fun <T> runOnBackgroundAndUpdateOnUI(backgroundBlock: suspend () -> T,
                                         uiBlock: (T) -> Unit) {
        launch(coroutineContexts.background, parent = this.parent) {
            if (isActive) {
                val value = withContext(parent) { backgroundBlock.invoke() }
                onUiContext { uiBlock(value) }
            }
        }
    }

    suspend fun <T> onUiContext(block: () -> T): T = withContext(coroutineContexts.ui) {
        block()
    }

    override fun onCleared() {
        super.onCleared()
        //cancel the coroutines
        launch {
            parent.cancel()
        }
    }
}