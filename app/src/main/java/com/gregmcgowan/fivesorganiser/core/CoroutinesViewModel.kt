package com.gregmcgowan.fivesorganiser.core

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

open class CoroutinesViewModel(private val ui: CoroutineContext,
                               private val background: CoroutineContext) : ViewModel() {

    private val parent: Job = Job()

    fun <T> runOnBackgroundAndUpdateOnUI(backgroundBlock: suspend () -> T,
                                         uiBlock: (T) -> Unit) {
        launch(background, parent = parent) {
            if (isActive) {
                val value = async(parent = parent) { backgroundBlock.invoke() }.await()
                withContext(ui) { uiBlock(value) }
            }
        }
    }

    suspend fun <T> onUiContext(block: () -> T): T = withContext(ui) {
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