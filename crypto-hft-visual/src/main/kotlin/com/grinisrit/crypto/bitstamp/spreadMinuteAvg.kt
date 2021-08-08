package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.models.Trade
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*
import space.kscience.plotly.models.BarMode
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.min

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

fun MinuteToValues.add(micros: Long, value: Float?) {
    val minutes = microsToMinutes(micros)
    getOrPut(minutes) { mutableListOf() }.add(Pair(micros, value))
}


@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val minuteToBAS1 = emptyMinuteToValues()
    val minuteToBS1 = emptyMinuteToValues()
    val minuteToAS1 = emptyMinuteToValues()

    val minuteToBAS2 = emptyMinuteToValues()
    val minuteToBS2 = emptyMinuteToValues()
    val minuteToAS2 = emptyMinuteToValues()

    val minuteToBAS3 = emptyMinuteToValues()
    val minuteToBS3 = emptyMinuteToValues()
    val minuteToAS3 = emptyMinuteToValues()

    val minuteToBuyTrades = emptyMinuteToValues()
    val minuteToSellTrades = emptyMinuteToValues()

    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F

    //val timeGeneral = mutableListOf<String>()

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

        val orderBookFlow = mongoClient.loadOrderBooks("btcusd")
        val tradeFlow = mongoClient.loadTrades("btcusd")

        launch {
            BitstampRefinedDataPublisher().orderBookFlow(orderBookFlow).collect { orderBook ->

                if (orderBook.isInvalid) {
                    return@collect
                }

                orderBook.getBidAskSpread(plotAmount1).let {
                    minuteToBAS1.add(orderBook.timestamp, it)
                }

                orderBook.getAskSideSpread(plotAmount1).let {
                    minuteToAS1.add(orderBook.timestamp, it)
                }

                orderBook.getBidSideSpread(plotAmount1).let {
                    minuteToBS1.add(orderBook.timestamp, it)
                }

                orderBook.getBidAskSpread(plotAmount2).let {
                    minuteToBAS2.add(orderBook.timestamp, it)
                }

                orderBook.getAskSideSpread(plotAmount2).let {
                    minuteToAS2.add(orderBook.timestamp, it)
                }

                orderBook.getBidSideSpread(plotAmount2).let {
                    minuteToBS2.add(orderBook.timestamp, it)
                }

                orderBook.getBidAskSpread(plotAmount3).let {
                    minuteToBAS3.add(orderBook.timestamp, it)
                }

                orderBook.getAskSideSpread(plotAmount3).let {
                    minuteToAS3.add(orderBook.timestamp, it)
                }

                orderBook.getBidSideSpread(plotAmount3).let {
                    minuteToBS3.add(orderBook.timestamp, it)
                }

            }
        }

        launch {
            BitstampRefinedDataPublisher().tradeFlow(tradeFlow).collect { trade ->
                with(trade) {
                    if (type == Trade.Type.BUY){
                        minuteToBuyTrades.add(timestamp, amount)
                    } else {
                        minuteToSellTrades.add(timestamp, amount)
                    }
                }
            }
        }

    }

    val (pointsBAS1, pointsLiquidity1) = minuteToBAS1.averagedMetricAndLiquidityPoints()
    val (pointsBAS2, pointsLiquidity2) = minuteToBAS2.averagedMetricAndLiquidityPoints()
    val (pointsBAS3, pointsLiquidity3) = minuteToBAS3.averagedMetricAndLiquidityPoints()

    // todo general time
    val (timeBAS1, valuesBAS1) = pointsBAS1
    val (timeAS1, valuesAS1) = minuteToAS1.averagedMetricAndLiquidityPoints().first
    val (timeBS1, valuesBS1) = minuteToBS1.averagedMetricAndLiquidityPoints().first

    val (timeBAS2, valuesBAS2) = pointsBAS2
    val (timeAS2, valuesAS2) = minuteToAS2.averagedMetricAndLiquidityPoints().first
    val (timeBS2, valuesBS2) = minuteToBS2.averagedMetricAndLiquidityPoints().first

    val (timeBAS3, valuesBAS3) = pointsBAS3
    val (timeAS3, valuesAS3) = minuteToAS3.averagedMetricAndLiquidityPoints().first
    val (timeBS3, valuesBS3) = minuteToBS3.averagedMetricAndLiquidityPoints().first

    val (timeLiquidity1, valuesLiquidity1) = pointsLiquidity1
    val (timeLiquidity2, valuesLiquidity2) = pointsLiquidity2
    val (timeLiquidity3, valuesLiquidity3) = pointsLiquidity3

    val (timeBuyTrades, valuesBuyTrades) = minuteToBuyTrades.tradesAmountsPoints()
    val (timeSellTrades, valuesSellTrades) = minuteToSellTrades.tradesAmountsPoints()

    val lowerBound = 20.0F

    val plot = Plotly.grid {

        plot {
            bar {
                x.set(timeBuyTrades)
                y.set(valuesBuyTrades.map {
                    min(it, lowerBound)
                })
                name = "Buy trades"
            }

            bar {
                x.set(timeSellTrades)
                y.set(valuesSellTrades.map {
                    min(it, lowerBound)
                })
                name = "Sell trades"
            }

            layout {
                title = "Bitstamp trades amounts"
                barmode = BarMode.group
                xaxis {
                    title = "Time, UTC"
                }
                yaxis {
                    title = "Trade volume"
                }
            }
        }

        plot {
            bar {
                x.set(timeBAS1)
                y.set(valuesBAS1)
                name = "Bid-ask spread"
            }

            bar {
                x.set(timeAS1)
                y.set(valuesAS1)
                name = "Ask spread"
            }

            bar {
                x.set(timeBS1)
                y.set(valuesBS1)
                name = "Bid spread"
            }

            layout {
                title = "Bitstamp time weighted spreads for $plotAmount1 BTC"
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
                x.set(timeBAS2)
                y.set(valuesBAS2)
                name = "Bid-ask spread"
            }

            bar {
                x.set(timeAS2)
                y.set(valuesAS2)
                name = "Ask spread"
            }

            bar {
                x.set(timeBS2)
                y.set(valuesBS2)
                name = "Bid spread"
            }

            layout {
                title = "Bitstamp time weighted spreads for $plotAmount2 BTC"
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
                x.set(timeBAS3)
                y.set(valuesBAS3)
                name = "Bid-ask spread"
            }

            bar {
                x.set(timeAS3)
                y.set(valuesAS3)
                name = "Ask spread"
            }

            bar {
                x.set(timeBS3)
                y.set(valuesBS3)
                name = "Bid spread"
            }

            layout {
                title = "Bitstamp time weighted spreads for $plotAmount3 BTC"
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
                x.set(timeLiquidity1.drop(1))
                y.set(valuesLiquidity1.drop(1))
                name = "$plotAmount1 BTC"
            }

            bar {
                x.set(timeLiquidity2.drop(1))
                y.set(valuesLiquidity2.drop(1))
                name = "$plotAmount2 BTC"
            }

            bar {
                x.set(timeLiquidity3.drop(1))
                y.set(valuesLiquidity3.drop(1))
                name = "$plotAmount3 BTC"
            }

            layout {
                title = "Bitstamp lack of liquidity"
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