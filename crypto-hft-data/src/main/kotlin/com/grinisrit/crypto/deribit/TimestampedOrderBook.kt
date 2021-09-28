package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedOrderBook(
    val receiving_datetime: Instant,
    val platform_data: DeribitBook,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
