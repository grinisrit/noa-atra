package com.grinisrit.crypto.coinbase


import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.bitstamp.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*


fun main(args: Array<String>) {

    val config = loadConf(args)



    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F


    lateinit var spreadMetrics: AmountToTimeWeightedSpreads

    runBlocking {

        val mongoClient = CoinbaseMongoClient(config.mongodb.getMongoDBServer())

        val snapshotsList = mongoClient.loadSnapshots("BTC-USD").toList()
        val updatesFlow = mongoClient.loadUpdates("BTC-USD")

        val orderBookFlow = CoinbaseRefinedDataPublisher().orderBookFlow(snapshotsList, updatesFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }

    }

    val platformName = "Coinbase"

    Plotly.grid {
        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics, platformName))
        }
        plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()

}