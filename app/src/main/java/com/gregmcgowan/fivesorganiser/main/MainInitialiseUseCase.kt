package com.gregmcgowan.fivesorganiser.main

import com.gregmcgowan.fivesorganiser.authentication.Authentication
import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainInitialiseUseCase @Inject constructor(
    private val authentication: Authentication,
    private val coroutineDispatchers: CoroutineDispatchers,
) {
    suspend fun execute() {
        withContext(coroutineDispatchers.io) {
            authentication.initialise()
        }
    }
}
