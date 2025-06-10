package com.gregmcgowan.fivesorganiser.core

import kotlinx.coroutines.CoroutineDispatcher

class CoroutineDispatchers(
    val ui: CoroutineDispatcher,
    val io: CoroutineDispatcher,
)
