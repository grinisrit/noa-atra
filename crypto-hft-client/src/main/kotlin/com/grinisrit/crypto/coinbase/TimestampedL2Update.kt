package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.TimestampedMarketData
import com.grinisrit.crypto.common.models.TimestampedData
import java.time.Instant

internal class TimestampedL2Update(
    val receiving_datetime: Instant,
    val platform_data: CoinbaseL2Update,
) {
    fun toTimestampedData(): TimestampedMarketData =
        TimestampedData(
            receiving_datetime,
            platform_data
        )
}
