package com.grinisrit.crypto.binance


import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*


fun main(args: Array<String>) {

    val config = loadConf(args)

    val plotAmount1 = 1
    val plotAmount2 = 5
    val plotAmount3 = 10

    lateinit var spreadMetrics: AmountToTimeWeightedSpreads
    lateinit var tradeMetrics: TimeWeightedTradesAmountsData

    runBlocking {

        val mongoClient = BinanceMongoClient(config.mongodb.getMongoDBServer())

        val snapshotsList = mongoClient.loadSnapshots("BTCUSDT").toList()
        val updatesFlow = mongoClient.loadUpdates("BTCUSDT")
        val unrefinedTradeFlow = mongoClient.loadTrades("BTCUSDT")

        val orderBookFlow = BinanceRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
        val tradeFlow = BinanceRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }

        launch {
            tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)
        }



    }

    val platformName = "Binance"

    Plotly.grid {

        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        plot(midPriceCandlestickPlot(spreadMetrics.filter { it.key == 1 }, platformName))
    }.makeFile()


}