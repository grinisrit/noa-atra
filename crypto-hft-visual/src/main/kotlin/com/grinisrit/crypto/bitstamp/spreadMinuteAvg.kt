package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*
import space.kscience.plotly.models.BarMode
import java.time.Instant
import java.time.temporal.ChronoUnit

typealias TimestampToValue = Pair<Long, Float?>
typealias GroupedValues = MutableList<TimestampToValue>
typealias MinuteToValues = MutableMap<Long, GroupedValues>

fun ofEpochMinute(epochMinute: Long): Instant =
    Instant.EPOCH.plus(epochMinute, ChronoUnit.MINUTES)

const val microMultiplier = 1e6.toLong()
const val minuteMultiplier = 60L * microMultiplier

fun microsToMinutes(micros: Long) =
    micros / minuteMultiplier

fun getLiquidityMetrics(
    groupedValues: GroupedValues,
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

fun MinuteToValues.averagedMetricAndLiquidityPoints(): Pair<Points, Points> {
    val metricPoints = getEmptyPoints()
    val liquidityPoints = getEmptyPoints()
    forEach { (minute, values) ->
        val (metric, liquidity) = getLiquidityMetrics(
            values,
            minute * minuteMultiplier to this[minute - 1]?.last()?.second
        )
        metricPoints.add(ofEpochMinute(minute).toString(), metric)
        liquidityPoints.add(ofEpochMinute(minute).toString(), liquidity)
    }
    return metricPoints to liquidityPoints
}

fun MinuteToValues.tradesAmountsPoints(): Points {
    val points = getEmptyPoints()
    forEach { (minute, values) ->
        points.add(ofEpochMinute(minute).toString(), values.map { it.second ?: 0.0F }.sum())
    }
    return points
}

fun emptyMinuteToValues() = mutableMapOf<Long, GroupedValues>()

fun MinuteToValues.add(micros: Long, value: Float) {
    val minutes = microsToMinutes(micros)
    getOrPut(minutes) { mutableListOf() }.add(Pair(micros, value))
}


@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val minuteToBAS = emptyMinuteToValues()
    val minuteToBS = emptyMinuteToValues()
    val minuteToAS = emptyMinuteToValues()

    val minuteToSmallerTrades = emptyMinuteToValues()
    val minuteToLargerTrades = emptyMinuteToValues()
    val minuteToAllTrades = emptyMinuteToValues()

    val plotAmount = 0.1F

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val orderBookFlow = mongoClient.loadOrderBooks("btcusd")
        val tradeFlow = mongoClient.loadTrades("btcusd")

        launch {
            BitstampRefinedDataPublisher().orderBookFlow(orderBookFlow).collect { orderBook ->

                if (orderBook.isInvalid) {
                    return@collect
                }

                orderBook.getBidAskSpread(plotAmount)?.let {
                    minuteToBAS.add(orderBook.timestamp, it)
                }

                orderBook.getAskSideSpread(plotAmount)?.let {
                    minuteToAS.add(orderBook.timestamp, it)
                }

                orderBook.getBidSideSpread(plotAmount)?.let {
                    minuteToBS.add(orderBook.timestamp, it)
                }

            }
        }

        launch {
            BitstampRefinedDataPublisher().tradeFlow(tradeFlow).collect { trade ->
                with(trade) {
                    minuteToAllTrades.add(timestamp, amount)
                    if (amount <= plotAmount) {
                        minuteToSmallerTrades.add(timestamp, amount)
                    } else {
                        minuteToLargerTrades.add(timestamp, amount)
                    }
                }
            }
        }

    }

    val (pointsBAS, pointsLiquidity) = minuteToBAS.averagedMetricAndLiquidityPoints()

    val (timeBAS, valuesBAS) = pointsBAS
    val (timeAS, valuesAS) = minuteToAS.averagedMetricAndLiquidityPoints().first
    val (timeBS, valuesBS) = minuteToBS.averagedMetricAndLiquidityPoints().first

    val (timeLiquidity, valuesLiquidity) = pointsLiquidity

    val (timeSmallerTrades, valuesSmallerTrades) = minuteToSmallerTrades.tradesAmountsPoints()
    val (timeLargerTrades, valuesLargerTrades) = minuteToLargerTrades.tradesAmountsPoints()
    val (timeAllTrades, valuesAllTrades) = minuteToAllTrades.tradesAmountsPoints()

    val plot = Plotly.grid {

        plot {
            bar {
                x.set(timeSmallerTrades)
                y.set(valuesSmallerTrades)
                name = "Trades with size <= $plotAmount BTC"
            }

            bar {
                x.set(timeLargerTrades)
                y.set(valuesLargerTrades)
                name = "Trades with size > $plotAmount BTC"
            }

            bar {
                x.set(timeAllTrades)
                y.set(valuesAllTrades)
                name = "All trades"
            }

            layout {
                title = "Bitstamp trades amounts"
                barmode = BarMode.group
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Trades/minute"
                }
            }
        }

        plot {
            bar {
                x.set(timeBAS)
                y.set(valuesBAS)
                name = "$plotAmount BTC"
            }

            layout {
                title = "Bitstamp $plotAmount BTC bid-ask spread"
                barmode = BarMode.group
                showlegend = true
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }

        plot {
            bar {
                x.set(timeAS)
                y.set(valuesAS)
                name = "$plotAmount BTC"
            }

            layout {
                title = "Bitstamp $plotAmount BTC ask spread"
                barmode = BarMode.group
                showlegend = true
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }


        plot {
            bar {
                x.set(timeBS)
                y.set(valuesBS)
                name = "$plotAmount BTC"
            }

            layout {
                title = "Bitstamp $plotAmount BTC bid spread"
                barmode = BarMode.group
                showlegend = true
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Spread, $/BTC"
                }
            }
        }


        plot {
            bar {
                x.set(timeLiquidity)
                y.set(valuesLiquidity)
                name = "$plotAmount BTC"
            }

            layout {
                title = "Lack of liquidity"
                barmode = BarMode.group
                showlegend = true
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Fraction of a minute"
                }
            }
        }

    }

    plot.makeFile()


}