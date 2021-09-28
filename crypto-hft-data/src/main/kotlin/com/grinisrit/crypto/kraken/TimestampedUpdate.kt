package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedUpdate(
    val receiving_datetime: Instant,
    val platform_data: KrakenBookUpdate,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
