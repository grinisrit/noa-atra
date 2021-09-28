package com.grinisrit.crypto.analysis

fun timeWeightedValueLiquidity(
    groupedValues: AggregatedValues,
    initialValue: TimestampToValue,
): Pair<Float, Float> {
    var currentTime = initialValue.first
    var currentValue = initialValue.second
    var buffer = 0F
    var liquidityTime = 0L
    groupedValues.forEach { (time, value) ->
        currentValue?.let {
            val interval = time - currentTime

            buffer += it * interval
            liquidityTime += interval
        }

        currentTime = time
        currentValue = value
    }

    currentValue?.let {
        val interval = (initialValue.first + minuteMultiplier - 1) - currentTime

        buffer += it * interval
        liquidityTime += interval
    }

    return (buffer / liquidityTime) to (1.0F - (liquidityTime.toFloat() / minuteMultiplier))
}

fun MinuteToValues.toTimeWeightedValuesAndLiquidityPoints(): Pair<Points, Points> {
    val metricPoints = emptyPoints()
    val liquidityPoints = emptyPoints()
    forEach { (minute, values) ->
        val (metric, liquidity) = timeWeightedValueLiquidity(
            values,
            minute * minuteMultiplier to this[minute - 1]?.last()?.second
        )
        metricPoints.add(minute, metric)
        liquidityPoints.add(minute, liquidity)
    }
    return metricPoints to liquidityPoints
}