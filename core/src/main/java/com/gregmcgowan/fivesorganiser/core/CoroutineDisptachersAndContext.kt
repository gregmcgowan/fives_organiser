package com.gregmcgowan.fivesorganiser.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineDisptachersAndContext(val ui: CoroutineDispatcher,
                                     val io: CoroutineDispatcher,
                                     val context: CoroutineContext = EmptyCoroutineContext)
