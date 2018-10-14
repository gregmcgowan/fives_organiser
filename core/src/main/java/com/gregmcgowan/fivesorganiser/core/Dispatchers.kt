package com.gregmcgowan.fivesorganiser.core

import kotlinx.coroutines.CoroutineDispatcher

class Dispatchers(val ui: CoroutineDispatcher,
                  val io: CoroutineDispatcher)