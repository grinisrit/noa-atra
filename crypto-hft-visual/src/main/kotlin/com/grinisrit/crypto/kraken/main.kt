package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.*
import space.kscience.plotly.*

fun main(args: Array<String>) {
    val config = loadConf(args)

    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F


    lateinit var spreadMetrics: AmountToTimeWeightedSpreads
    lateinit var tradeMetrics: TimeWeightedTradesAmountsData

    runBlocking {

        val mongoClient = KrakenMongoClient(config.mongodb.getMongoDBServer())

        val snapshotsList = mongoClient.loadSnapshots("XBT/USD").toList()
        val updatesFlow = mongoClient.loadUpdates("XBT/USD")
        val unrefinedTradeFlow = mongoClient.loadTrades("XBT/USD")

        val orderBookFlow = KrakenRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
        val tradeFlow = KrakenRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }

        launch {
            tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)
        }

    }

    val platformName = "Kraken"

    Plotly.grid {
        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics, platformName))
        }

        plot(timeWeightedMidPricesPlot(spreadMetrics, platformName))
    }.makeFile()

}