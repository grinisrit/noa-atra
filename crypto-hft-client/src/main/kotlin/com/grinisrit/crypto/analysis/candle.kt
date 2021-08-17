package com.grinisrit.crypto.analysis

class Candle(
    val open: Float,
    val close: Float,
    val low: Float,
    val high: Float
)

class Candles {
    val timeList: MutableList<Long> = mutableListOf()
    val openList: MutableList<Float> = mutableListOf()
    val closeList: MutableList<Float> = mutableListOf()
    val lowList: MutableList<Float> = mutableListOf()
    val highList: MutableList<Float> = mutableListOf()

    fun add(
        time: Long,
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

    fun add(candle: Candle, time: Long) {
        with(candle) {
            timeList.add(time)
            openList.add(open)
            closeList.add(close)
            lowList.add(low)
            highList.add(high)
        }
    }
}

fun countCandle(values: List<Float>, initialOpen: Float): Candle {
    return Candle(
        initialOpen,
        values.last(),
        values.minOf { it },
        values.maxOf { it }
    )
}