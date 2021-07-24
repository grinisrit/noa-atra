package com.grinisrit.crypto.common

import java.time.Instant

data class MarketData<out T : PlatformData>(
    val receiving_datetime: Instant,
    val platform_data: T,
)
