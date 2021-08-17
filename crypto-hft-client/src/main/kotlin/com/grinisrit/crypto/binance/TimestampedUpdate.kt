package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedUpdate(
    val receiving_datetime: Instant,
    val platform_data: BinanceBookUpdate,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
