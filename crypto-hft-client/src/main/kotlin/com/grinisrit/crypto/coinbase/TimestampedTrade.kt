package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedTrade(
    val receiving_datetime: Instant,
    val platform_data: CoinbaseMatch,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
