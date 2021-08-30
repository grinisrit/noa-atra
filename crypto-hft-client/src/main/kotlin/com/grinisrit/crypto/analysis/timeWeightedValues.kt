package com.grinisrit.crypto.analysis

import com.grinisrit.crypto.common.models.OrderBook
import com.grinisrit.crypto.common.models.Trade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class MinuteAggregatedValues {
    /*
    val ask = emptyAggregatedValues()
    val bid = emptyAggregatedValues()

     */
    val bidAsk = emptyAggregatedValues()
    val midPrice = emptyAggregatedValues()
/*
    var previousMinuteLastAsk: Float? = null
    var previousMinuteLastBid: Float? = null

 */
    var previousMinuteLastBidAsk: Float? = null
    var previousMinuteLastMidPrice: Float? = null

    fun clear() {
        /*
        ask.clear()
        bid.clear()

         */
        bidAsk.clear()
        midPrice.clear()
    }
}

class TimeWeightedValues {
    val time = mutableListOf<Long>()
    val liquidity = mutableListOf<Float>()
    /*
    val ask = mutableListOf<Float>()
    val bid = mutableListOf<Float>()

     */
    //val midPrice = mutableListOf<Float>()
    val bidAsk = mutableListOf<Float>()
}

// TODO
class TimeWeightedTradesAmountsData(
    val time: LongArray,
    val buy: FloatArray,
    val sell: FloatArray,
)

// TODO
class SpreadData(
    val minuteAggregatedValues : MinuteAggregatedValues,
    val timeWeightedValues: TimeWeightedValues,
    val candles: Candles,
)

// TODO
typealias AmountToTimeWeightedSpreads = Map<Float, Pair<TimeWeightedValues, Candles>>

suspend fun countTimeWeightedMetricsAndLiquidity(
    orderBookFlow: Flow<OrderBook>,
    amountsList: List<Float>
): AmountToTimeWeightedSpreads {

    val amountToSpreadsData = amountsList.associateWith {
        SpreadData(MinuteAggregatedValues(), TimeWeightedValues(), Candles())
    }

    var lastMinute = -1L

    orderBookFlow.collect { orderBook ->
        val timestamp = orderBook.timestamp

        val minute = microsToMinutes(timestamp)

        // TODO(log)
        println(instantOfEpochMinute(minute))

        if (minute > lastMinute) {
            if (lastMinute != -1L && minute == lastMinute + 1) {

                val initialTime = minutesToMicros(lastMinute)

                amountToSpreadsData.forEach { (_, spreadData) ->
                    val minuteAggregatedSpreads = spreadData.minuteAggregatedValues
                    val timeWeightedSpreads = spreadData.timeWeightedValues

                    if (minuteAggregatedSpreads.midPrice.size == 0){
                        return@forEach
                    }

                    timeWeightedSpreads.time.add(minute)

                    /*

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

                     */

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
                        /*
                        timeWeightedSpreads.bidAsk.add(0.0F)
                        timeWeightedSpreads.liquidity.add(0.0F)

                         */
                    }

                    val previousLastMidPrice = minuteAggregatedSpreads.previousMinuteLastMidPrice

                    if (previousLastMidPrice != null) {
                        val candle = countCandle(minuteAggregatedSpreads.midPrice.mapNotNull {
                            it.second
                        }, previousLastMidPrice)

                        spreadData.candles.add(candle, minute)
                    }

                }

            }

            amountToSpreadsData.forEach { (_, spreadData) ->
                val minuteAggregatedSpreads = spreadData.minuteAggregatedValues
                /*
                minuteAggregatedSpreads.previousMinuteLastAsk = minuteAggregatedSpreads.ask.lastOrNull()?.second
                minuteAggregatedSpreads.previousMinuteLastBid = minuteAggregatedSpreads.bid.lastOrNull()?.second
                 */

                minuteAggregatedSpreads.previousMinuteLastBidAsk =
                    minuteAggregatedSpreads.bidAsk.lastOrNull()?.second
                minuteAggregatedSpreads.previousMinuteLastMidPrice =
                    minuteAggregatedSpreads.midPrice.lastOrNull()?.second

                spreadData.minuteAggregatedValues.clear()
            }

        }

        lastMinute = minute
        // TODO

        if (orderBook.isInvalid) {
            // TODO log
            println("invalid orderbook")

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
            /*
            spreadData.minuteAggregatedSpreads.ask.add(
                timestamp to orderBook.getAskSpread(amount)
            )
            spreadData.minuteAggregatedSpreads.bid.add(
                timestamp to orderBook.getBidSpread(amount)
            )

             */
            spreadData.minuteAggregatedValues.bidAsk.add(
                timestamp to orderBook.getBidAskSpread(amount)
            )

            spreadData.minuteAggregatedValues.midPrice.add(
                timestamp to orderBook.getMidPrice(amount)
            )
        }

    }

    return amountToSpreadsData.map { (amount, spreadData) ->
        amount to (spreadData.timeWeightedValues to spreadData.candles)
    }.toMap()
}


// TODO better
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
