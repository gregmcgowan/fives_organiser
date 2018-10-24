package com.gregmcgowan.fivesorganiser.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

open class CoroutinesViewModel(private val coroutineDisptachersAndContext: CoroutineDisptachersAndContext) : ViewModel() {

    private val parent: Job = Job()

    private val uiScope = CoroutineScope(coroutineDisptachersAndContext.ui + parent)

    fun <T> launch(backgroundBlock: suspend () -> T,
                   uiBlock: (T) -> Unit) {
        uiScope.launch(coroutineDisptachersAndContext.context) {
            if (isActive) {
                uiBlock(async(coroutineDisptachersAndContext.io) { backgroundBlock.invoke() }.await())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parent.cancel()
    }

}