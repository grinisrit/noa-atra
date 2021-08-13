package com.grinisrit.crypto.coinbase

import java.time.Instant
import java.util.concurrent.TimeUnit

fun Instant.toEpochMicro() =
    TimeUnit.SECONDS.toMicros(epochSecond) + TimeUnit.NANOSECONDS.toMicros(nano.toLong())