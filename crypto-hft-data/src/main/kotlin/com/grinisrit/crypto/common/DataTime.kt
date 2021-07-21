package com.grinisrit.crypto.common

import java.time.Instant

data class DataTime<out T : ChannelData>(
    val receiving_datetime: Instant,
    val data: T,
)