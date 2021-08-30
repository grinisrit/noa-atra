package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.*
import space.kscience.plotly.*

suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)

    val amounts = listOf(1, 5, 10)

    val mongoClient = KrakenMongoClient(config.mongodb.getMongoDBServer())

    val snapshotsList = mongoClient.loadSnapshots("XBT/USD").toList()
    val updatesFlow = mongoClient.loadUpdates("XBT/USD")
    val unrefinedTradeFlow = mongoClient.loadTrades("XBT/USD")

    val orderBookFlow = KrakenRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val tradeFlow = KrakenRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, amounts)

    val tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)


    val platformName = "Kraken"

    Plotly.grid {
        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        plot(midPriceCandlestickPlot(spreadMetrics, platformName))
    }.makeFile()

}