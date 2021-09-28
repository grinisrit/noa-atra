package com.grinisrit.crypto.finery

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedUpdates(
    val receiving_datetime: Instant,
    val platform_data: FineryUpdates,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
