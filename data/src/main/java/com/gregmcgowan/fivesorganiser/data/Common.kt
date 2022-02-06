package com.gregmcgowan.fivesorganiser.data

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
