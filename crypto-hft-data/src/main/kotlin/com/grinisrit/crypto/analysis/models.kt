package com.grinisrit.crypto.analysis

// TODO could be tensor
typealias Points = Pair<List<Long>, List<Float>>
typealias MutablePoints = Pair<MutableList<Long>, MutableList<Float>>

fun emptyPoints() = Pair(mutableListOf<Long>(), mutableListOf<Float>())

fun MutablePoints.add(time: Long, data: Float){
    first.add(time)
    second.add(data)
}

typealias TimestampToValue = Pair<Long, Float?>
typealias AggregatedValues = MutableList<TimestampToValue>
typealias MinuteToValues = MutableMap<Long, AggregatedValues>

fun emptyAggregatedValues() = mutableListOf<TimestampToValue>()

fun emptyMinuteToValues() = mutableMapOf<Long, AggregatedValues>()

fun MinuteToValues.add(micros: Long, value: Float?) {
    val minutes = microsToMinutes(micros)
    getOrPut(minutes) { mutableListOf() }.add(Pair(micros, value))
}
