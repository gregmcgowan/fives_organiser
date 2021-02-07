package com.gregmcgowan.fivesorganiser.data

import com.gregmcgowan.fivesorganiser.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal const val ID_KEY = "auto_generated_id"

data class DataUpdate<T>(
        val changes: List<DataChange<T>>
)

data class DataChange<T>(
        val type: DataChangeType,
        val data: T
)

sealed class DataChangeType {

    object Added : DataChangeType()
    object Modified : DataChangeType()
    object Removed : DataChangeType()

}

fun <T> Flow<DataUpdate<T>>.asEither(): Flow<Either<Throwable, DataUpdate<T>>> =
        this.map<DataUpdate<T>, Either<Throwable, DataUpdate<T>>> { Either.Right(it) }
                .catch { e: Throwable -> emit(Either.Left(e)) }