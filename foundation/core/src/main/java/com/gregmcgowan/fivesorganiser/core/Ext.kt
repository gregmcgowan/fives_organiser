package com.gregmcgowan.fivesorganiser.core

import kotlin.coroutines.cancellation.CancellationException

@Suppress("TooGenericExceptionCaught", "RethrowCaughtException")
suspend fun <T> runCatchingSafely(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
