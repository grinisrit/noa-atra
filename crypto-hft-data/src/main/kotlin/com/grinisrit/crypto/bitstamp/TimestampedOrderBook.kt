package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedOrderBook(
    val receiving_datetime: Instant,
    val platform_data: BitstampOrderBook,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
