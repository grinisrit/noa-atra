package com.grinisrit.crypto

import java.text.SimpleDateFormat
import java.util.*

fun cbTimeToDate(cbTime: String): Date {
    val format = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")

    val millisTime = cbTime.dropLast(4) + "Z"

    return format.parse(millisTime)
}