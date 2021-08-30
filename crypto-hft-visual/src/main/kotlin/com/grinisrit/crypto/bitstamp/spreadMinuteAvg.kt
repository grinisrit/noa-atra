package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.*
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.*
import space.kscience.plotly.*

@OptIn(UnstablePlotlyAPI::class)
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amounts = listOf(1F, 5F, 10F)


    val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
    val unrefinedOrderBookFlow = mongoClient.loadOrderBooks("btcusd")
    val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)
    val unrefinedTradeFlow = mongoClient.loadTrades("btcusd")
    val tradeFlow = BitstampRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)


    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, amounts)


    val tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)


    val platformName = "Bitstamp"

    Plotly.grid {

        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        plot(midPriceCandlestickPlot(spreadMetrics, platformName))
    }.makeFile()

}