package com.grinisrit.crypto.analysis

import java.time.Instant
import java.time.temporal.ChronoUnit

fun instantOfEpochMicro(epochMicro: Long): Instant =
    Instant.EPOCH.plus(epochMicro, ChronoUnit.MICROS)

fun instantOfEpochMinute(epochMinute: Long): Instant =
    Instant.EPOCH.plus(epochMinute, ChronoUnit.MINUTES)

const val microMultiplier = 1e6.toLong()
const val minuteMultiplier = 60L * microMultiplier

fun microsToMinutes(micros: Long) = micros / minuteMultiplier
fun minutesToMicros(minutes: Long) = minutes * minuteMultiplier
