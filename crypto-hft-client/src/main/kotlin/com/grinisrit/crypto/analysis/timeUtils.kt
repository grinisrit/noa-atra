package com.grinisrit.crypto.analysis

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

fun instantOfEpochMicro(epochMicro: Long): Instant =
    Instant.EPOCH.plus(epochMicro, ChronoUnit.MICROS)

fun instantOfEpochMinute(epochMinute: Long): Instant =
    Instant.EPOCH.plus(epochMinute, ChronoUnit.MINUTES)

fun Instant.toEpochMicro() =
    TimeUnit.SECONDS.toMicros(epochSecond) + TimeUnit.NANOSECONDS.toMicros(nano.toLong())

const val microMultiplier = 1e6.toLong()
const val minuteMultiplier = 60L * microMultiplier

fun microsToMinutes(micros: Long) = micros / minuteMultiplier
fun minutesToMicros(minutes: Long) = minutes * minuteMultiplier
