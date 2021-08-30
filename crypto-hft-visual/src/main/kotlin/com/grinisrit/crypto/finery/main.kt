package com.grinisrit.crypto.finery

import com.grinisrit.crypto.analysis.AmountToTimeWeightedSpreads
import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.analysis.getBidAskSpread
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.timeWeightedLiquidityPlot
import com.grinisrit.crypto.common.timeWeightedSpreadsPlot
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.Plotly
import space.kscience.plotly.grid
import space.kscience.plotly.makeFile

suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)

    val amounts = listOf(1F, 5F, 10F)

    val mongoClient = FineryMongoClient(config.mongodb.getMongoDBServer())

    val snapshotsList = mongoClient.loadSnapshots("BTC-USD").toList()
    val updatesFlow = mongoClient.loadUpdates("BTC-USD")

    val orderBookFlow = FineryRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)

    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, amounts)

    val platformName = "Finery Markets"

    Plotly.grid {
        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }
        plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()

}