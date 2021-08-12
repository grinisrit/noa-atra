package com.grinisrit.crypto.analysis

class CandlePoints {
    val timeList: MutableList<String> = mutableListOf()
    val openList: MutableList<Float> = mutableListOf()
    val closeList: MutableList<Float> = mutableListOf()
    val lowList: MutableList<Float> = mutableListOf()
    val highList: MutableList<Float> = mutableListOf()
}

fun CandlePoints.add(
    time: String,
    open: Float,
    close: Float,
    low: Float,
    high: Float
) {
    timeList.add(time)
    openList.add(open)
    closeList.add(close)
    lowList.add(low)
    highList.add(high)
}

fun MinuteToValues.toCandlePoints(): CandlePoints {
    val candlePoints = CandlePoints()
    var close = values.first().first().second!! // TODO()
    forEach { (minute, values) ->
        val open = close
        close = values.last().second!!
        val low = values.minOf { it.second!! }
        val high = values.maxOf { it.second!! }
        candlePoints.add(
            instantOfEpochMinute(minute).toString(),
            open,
            close,
            low,
            high
        )
    }
    return candlePoints
}