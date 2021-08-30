package com.grinisrit.crypto.binance


import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*


suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amounts = listOf(1F, 5F, 10F)

    val mongoClient = BinanceMongoClient(config.mongodb.getMongoDBServer())

    val snapshotsList = mongoClient.loadSnapshots("BTCUSDT").toList()
    val updatesFlow = mongoClient.loadUpdates("BTCUSDT")
    val unrefinedTradeFlow = mongoClient.loadTrades("BTCUSDT")

    val orderBookFlow = BinanceRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val tradeFlow = BinanceRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)


    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, amounts)

    val tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)


    val platformName = "Binance"

    Plotly.grid {

        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        plot(midPriceCandlestickPlot(spreadMetrics.filter { it.key == 1.0F }, platformName))
    }.makeFile()


}