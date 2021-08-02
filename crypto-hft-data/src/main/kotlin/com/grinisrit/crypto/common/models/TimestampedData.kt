package com.grinisrit.crypto.common.models

import java.time.Instant

data class TimestampedData<out T : PlatformData>(
    val receiving_datetime: Instant,
    val platform_data: T,
)
