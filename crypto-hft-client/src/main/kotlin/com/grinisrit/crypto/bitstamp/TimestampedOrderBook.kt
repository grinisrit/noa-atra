package com.grinisrit.crypto.bitstamp

import java.time.Instant

data class TimestampedOrderBook(
    val receiving_datetime: Instant,
    val platform_data: OrderBook,
)
