package com.grinisrit.crypto

import java.text.SimpleDateFormat
import java.util.*


fun cbTimeToDate(cbTime: String): Date {
    val datePattern = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    val millisTime = cbTime.dropLast(4) + "Z"

    return datePattern.parse(millisTime)
}

fun cbFormat(date: Date): String {
    val datePattern = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    return datePattern.format(date)
}

fun cbParse(dateString: String): Date {
    val datePattern = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    return datePattern.parse(dateString)
}
