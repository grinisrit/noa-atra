package com.grinisrit.crypto.analysis

fun MinuteToValues.tradesAmountsPoints(): Points {
    val points = emptyPoints()
    forEach { (minute, values) ->
        points.add(minute, values.map { it.second ?: 0.0F }.sum())
    }
    return points
}
