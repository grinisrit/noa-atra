package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.models.*
import com.grinisrit.crypto.common.mongo.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import space.kscience.plotly.*
import space.kscience.plotly.Plotly.plot
import space.kscience.plotly.models.Bar
import space.kscience.plotly.models.BarMode
import kotlin.math.min

class MinuteAggregatedSpreads {
    val ask = emptyAggregatedValues()
    val bid = emptyAggregatedValues()
    val bidAsk = emptyAggregatedValues()

    var previousMinuteLastAsk: Float? = null
    var previousMinuteLastBid: Float? = null
    var previousMinuteLastBidAsk: Float? = null

    fun clear() {
        ask.clear()
        bid.clear()
        bidAsk.clear()
    }
}

class TimeWeightedSpreads {
    val time = mutableListOf<Long>()
    val liquidity = mutableListOf<Float>()
    val ask = mutableListOf<Float>()
    val bid = mutableListOf<Float>()
    val bidAsk = mutableListOf<Float>()
}

class TimeWeightedTradesAmountsData(
    val time: LongArray,
    val buy: FloatArray,
    val sell: FloatArray,
)

class SpreadData(
    val minuteAggregatedSpreads : MinuteAggregatedSpreads,
    val timeWeightedSpreads: TimeWeightedSpreads
)

typealias AmountToTimeWeightedSpreads = Map<Float, TimeWeightedSpreads>
typealias AmountToSpreadData = Map<Float, SpreadData>

suspend fun countTimeWeightedMetricsAndLiquidity(
    orderBookFlow: Flow<OrderBook>,
    amountsList: List<Float>
): AmountToTimeWeightedSpreads {

    val amountToSpreadsData = amountsList.associateWith {
        SpreadData(MinuteAggregatedSpreads(), TimeWeightedSpreads())
    }

    var lastMinute = -1L

    orderBookFlow.collect { orderBook ->
        val timestamp = orderBook.timestamp

        val minute = microsToMinutes(timestamp)

        if (minute > lastMinute) {
            if (lastMinute != -1L && minute == lastMinute + 1) {
                val initialTime = minutesToMicros(lastMinute)
                amountToSpreadsData.forEach { (_, spreadData) ->
                    val minuteAggregatedSpreads = spreadData.minuteAggregatedSpreads
                    val timeWeightedSpreads = spreadData.timeWeightedSpreads

                    timeWeightedSpreads.time.add(minute)

                    val previousLastAsk = minuteAggregatedSpreads.previousMinuteLastAsk
                    if (previousLastAsk != null) {
                        timeWeightedSpreads.ask.add(
                            timeWeightedValueLiquidity(
                                minuteAggregatedSpreads.ask,
                                initialTime to previousLastAsk
                            ).first
                        )
                    } else {
                        timeWeightedSpreads.ask.add(0.0F)
                    }


                    val previousLastBid = minuteAggregatedSpreads.previousMinuteLastBid
                    if (previousLastBid != null) {
                        timeWeightedSpreads.bid.add(
                            timeWeightedValueLiquidity(
                                minuteAggregatedSpreads.bid,
                                initialTime to previousLastBid
                            ).first
                        )
                    } else {
                        timeWeightedSpreads.bid.add(0.0F)
                    }

                    val previousLastBidAsk = minuteAggregatedSpreads.previousMinuteLastBidAsk
                    if (previousLastBidAsk != null) {
                        val (bidAskValue, liquidityValue) =
                            timeWeightedValueLiquidity(
                                minuteAggregatedSpreads.bidAsk,
                            initialTime to previousLastBidAsk
                        )
                        timeWeightedSpreads.bidAsk.add(bidAskValue)
                        timeWeightedSpreads.liquidity.add(liquidityValue)
                    } else {
                        timeWeightedSpreads.bidAsk.add(0.0F)
                        timeWeightedSpreads.liquidity.add(0.0F)
                    }
                    /*
                    minuteAggregatedSpreads.previousMinuteLastAsk = minuteAggregatedSpreads.ask.lastOrNull()?.second
                    minuteAggregatedSpreads.previousMinuteLastBid = minuteAggregatedSpreads.bid.lastOrNull()?.second
                    minuteAggregatedSpreads.previousMinuteLastBidAsk = minuteAggregatedSpreads.bidAsk.lastOrNull()?.second

                     */
                }

            } /* else {
                amountToSpreadsData.forEach { (_, spreadData) ->
                    val minuteAggregatedSpreads = spreadData.minuteAggregatedSpreads
                    minuteAggregatedSpreads.previousMinuteLastAsk = null
                    minuteAggregatedSpreads.previousMinuteLastBid = null
                    minuteAggregatedSpreads.previousMinuteLastBidAsk = null
                }
            }
            */

            amountToSpreadsData.forEach { (_, spreadData) ->
                val minuteAggregatedSpreads = spreadData.minuteAggregatedSpreads
                minuteAggregatedSpreads.previousMinuteLastAsk = minuteAggregatedSpreads.ask.lastOrNull()?.second
                minuteAggregatedSpreads.previousMinuteLastBid = minuteAggregatedSpreads.bid.lastOrNull()?.second
                minuteAggregatedSpreads.previousMinuteLastBidAsk = minuteAggregatedSpreads.bidAsk.lastOrNull()?.second
            }



            amountToSpreadsData.forEach { (_, spreadData) ->
                val minuteAggregatedSpreads = spreadData.minuteAggregatedSpreads
                minuteAggregatedSpreads.ask.clear()
                minuteAggregatedSpreads.bid.clear()
                minuteAggregatedSpreads.bidAsk.clear()
            }
        }

        lastMinute = minute
        // TODO

        if (orderBook.isInvalid) {

            /*
            val timestampToNull = timestamp to null
            amountToSpreadsData.forEach { (_, spreadData) ->
                val minuteAggregatedSpreads = spreadData.minuteAggregatedSpreads
                minuteAggregatedSpreads.ask.add(timestampToNull)
                minuteAggregatedSpreads.bid.add(timestampToNull)
                minuteAggregatedSpreads.bidAsk.add(timestampToNull)
            }

             */
            return@collect
        }


        amountToSpreadsData.forEach { (amount, spreadData) ->
            spreadData.minuteAggregatedSpreads.ask.add(
                timestamp to orderBook.getAskSpread(amount)
            )
            spreadData.minuteAggregatedSpreads.bid.add(
                timestamp to orderBook.getBidSpread(amount)
            )
            spreadData.minuteAggregatedSpreads.bidAsk.add(
                timestamp to orderBook.getBidAskSpread(amount)
            )
        }

    }
    // TODO better time

    return amountToSpreadsData.map { (amount, spreadData) ->
        amount to spreadData.timeWeightedSpreads
    }.toMap()
}
/*
suspend fun countTimeWeightedTradesAmounts(tradeFlow: Flow<Trade>): TimeWeightedTradesAmountsData {
    val minuteToBuyTrades = emptyMinuteToValues()
    val minuteToSellTrades = emptyMinuteToValues()
    tradeFlow.collect { trade ->
        with(trade) {
            if (type == Trade.Type.BUY) {
                minuteToBuyTrades.add(timestamp, amount)
                minuteToSellTrades.add(timestamp, 0.0F)
            } else {
                minuteToSellTrades.add(timestamp, amount)
                minuteToBuyTrades.add(timestamp, 0.0F)
            }
        }
    }
    // TODO better time
    val (time, valuesBuyTrades) = minuteToBuyTrades.tradesAmountsPoints()
    val (_, valuesSellTrades) = minuteToSellTrades.tradesAmountsPoints()

    return TimeWeightedTradesAmountsData(
        time.toLongArray(),
        valuesBuyTrades.toFloatArray(),
        valuesSellTrades.toFloatArray()
    )
}

 */

// TODO??
fun timeWeightedSpreadsPlot(
    amount: Float,
    timeWeightedSpreads: TimeWeightedSpreads,
    platformName: String
): Plot {
    val time = timeWeightedSpreads.time.map { instantOfEpochMinute(it).toString() }
    return plot {
        bar {
            x.set(time)
            y.set(timeWeightedSpreads.bidAsk)
            name = "Bid-ask spread"
        }

        bar {
            x.set(time)
            y.set(timeWeightedSpreads.ask)
            name = "Ask spread"
        }

        bar {
            x.set(time)
            y.set(timeWeightedSpreads.bid)
            name = "Bid spread"
        }

        // TODO symbols
        layout {
            title = "$platformName time weighted spreads for $amount BTC"
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
}

fun timeWeightedLiquidityPlot(
    amountToTimeWeightedSpreads: AmountToTimeWeightedSpreads,
    platformName: String,
): Plot {
    // TODO symbols
    val traces = amountToTimeWeightedSpreads.map { (amount, spreadData) ->
        Bar {
            x.set(spreadData.time.map { instantOfEpochMinute(it).toString() })
            y.set(spreadData.liquidity)
            name = "$amount BTC"
        }
    }
    return plot {
        traces(traces)

        layout {
            title = "$platformName lack of liquidity"
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

fun timeWeightedTradesPlot(
    timeWeightedTradesAmountsData: TimeWeightedTradesAmountsData,
    platformName: String,
    lowerBound: Float? = null
): Plot {
    val time = timeWeightedTradesAmountsData.time.map {
        instantOfEpochMinute(it).toString()
    }
    // TODO symbols
    return plot {
        bar {
            x.set(time)
            y.set(timeWeightedTradesAmountsData.buy.map {
                if (lowerBound == null) {
                    it
                } else {
                    min(it, lowerBound)
                }
            })
            name = "buy"
        }

        bar {
            x.set(time)
            y.set(timeWeightedTradesAmountsData.sell.map {
                if (lowerBound == null) {
                    it
                } else {
                    min(it, lowerBound)
                }
            })
            name = "sell"
        }

        layout {
            title = "$platformName trades amounts"
            barmode = BarMode.group
            xaxis {
                title = "Time, UTC"
            }
            yaxis {
                title = "Trade volume"
            }
        }

    }
}


@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F


    lateinit var spreadMetrics: AmountToTimeWeightedSpreads
    lateinit var tradeMetrics: TimeWeightedTradesAmountsData

    runBlocking {
        println(1)
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
        println(2)
        val unrefinedOrderBookFlow = mongoClient.loadOrderBooks("btcusd")
        val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)
        val unrefinedTradeFlow = mongoClient.loadTrades("btcusd")
        val tradeFlow = BitstampRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }
        /*
        launch {
            tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)
        }

         */

    }

    val platformName = "Bitstamp"

    Plotly.grid {
      //  plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))
        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics, platformName))
        }
        plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()

}