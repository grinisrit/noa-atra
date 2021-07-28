package com.grinisrit.crypto.finery

import com.grinisrit.crypto.common.models.DataType
import com.grinisrit.crypto.common.models.PlatformData
import kotlinx.serialization.Serializable

interface FineryData : PlatformData

@Serializable
data class BookLevel(
    val price: Double,
    val size: Double,
)

@Serializable
data class BookUpdate(
    val action: Char,
    val price: Double,
    val size: Double,
)

@Serializable
data class SnapshotData(
    val bids: List<BookLevel>,
    val asks: List<BookLevel>,
)

@Serializable
data class Snapshot(
    val feed: Char,
    val feedId: Long,
    val dataType: Char,
    val data: SnapshotData,
) : FineryData {
    override val type = FineryDataType.snapshot
}

@Serializable
data class UpdatesData(
    val bids: List<BookUpdate>,
    val asks: List<BookUpdate>,
)

@Serializable
data class Updates(
    val feed: Char,
    val feedId: Long,
    val dataType: Char,
    val data: UpdatesData,
) : FineryData {
    override val type = FineryDataType.updates
}
