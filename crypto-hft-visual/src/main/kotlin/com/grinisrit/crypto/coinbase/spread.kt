package com.grinisrit.crypto.coinbase



import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import space.kscience.plotly.*


suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amounts = listOf(1F, 5F, 10F)


    val mongoClient = CoinbaseMongoClient(config.mongodb.getMongoDBServer())

    val snapshotsList = mongoClient.loadSnapshots("BTC-USD").toList()
    val updatesFlow = mongoClient.loadUpdates("BTC-USD")
    val unrefinedTradeFlow = mongoClient.loadTrades("BTC-USD")

    val orderBookFlow = CoinbaseRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val tradeFlow = CoinbaseRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)


    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, amounts)

    val tradeMetrics = countTimeWeightedTradesAmounts(tradeFlow)


    val platformName = "Coinbase"

    Plotly.grid {

        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 35.0F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        //plot(timeWeightedLiquidityPlot(spreadMetrics, platformName))
    }.makeFile()


}