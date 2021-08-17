package com.grinisrit.crypto.finery

import com.grinisrit.crypto.analysis.AmountToTimeWeightedSpreads
import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.analysis.getBidAskSpread
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.timeWeightedLiquidityPlot
import com.grinisrit.crypto.common.timeWeightedSpreadsPlot
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.Plotly
import space.kscience.plotly.grid
import space.kscience.plotly.makeFile

fun main(args: Array<String>) {
    val config = loadConf(args)

    val plotAmount1 = 1.0F
    val plotAmount2 = 5.0F
    val plotAmount3 = 10.0F


    lateinit var spreadMetrics: AmountToTimeWeightedSpreads

    runBlocking {

        val mongoClient = FineryMongoClient(config.mongodb.getMongoDBServer())

        val snapshotsList = mongoClient.loadSnapshots("BTC-USD").toList()
        val updatesFlow = mongoClient.loadUpdates("BTC-USD")

        val orderBookFlow = FineryRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)

        launch {
            spreadMetrics =
                countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1, plotAmount2, plotAmount3))
        }

    }

    val platformName = "Finery Markets"

    Plotly.grid {
        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }
        plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()

}