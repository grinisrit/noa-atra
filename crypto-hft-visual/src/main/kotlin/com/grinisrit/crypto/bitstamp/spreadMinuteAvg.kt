package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.*
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.*
import space.kscience.plotly.*

@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>) {

    val config = loadConf(args)

    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F


    lateinit var spreadMetrics: AmountToTimeWeightedSpreads
    lateinit var tradeMetrics: TimeWeightedTradesAmountsData

    runBlocking {
        val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
        val unrefinedOrderBookFlow = mongoClient.loadOrderBooks("btcusd")
        val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)
        val unrefinedTradeFlow = mongoClient.loadTrades("btcusd")
        val tradeFlow = BitstampRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }

        launch {
            tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)
        }

    }

    val platformName = "Bitstamp"

    Plotly.grid {
        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))
        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics, platformName))
        }
        plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()

}