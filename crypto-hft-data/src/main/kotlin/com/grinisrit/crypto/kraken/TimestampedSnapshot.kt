package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedSnapshot(
    val receiving_datetime: Instant,
    val platform_data: KrakenBookSnapshot,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
