package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.analysis.*
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.runBlocking
import space.kscience.plotly.*

@OptIn(UnstablePlotlyAPI::class)
fun main(args: Array<String>){
    val config = loadConf(args)

    val plotAmount1 = 1000.0F

    val mongoClient = runBlocking {
        DeribitMongoClient(config.mongodb.getMongoDBServer())
    }

    val unrefinedOrderBookFlow = mongoClient.loadOrderBooks("BTC-PERPETUAL")
    val orderBookFlow = DeribitRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)

    val unrefinedTradeFlow = mongoClient.loadTrades("BTC-PERPETUAL")
    val tradeFlow = DeribitRefinedDataPublisher.tradeFlow(unrefinedTradeFlow)

    val spreadMetrics = runBlocking {
        countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(plotAmount1))
    }

    val tradeMetrics = runBlocking {
        countTimeWeightedTradesAmounts(tradeFlow)
    }

    val platformName = "Deribit"

    Plotly.grid {

        plot(timeWeightedTradesPlot(tradeMetrics, platformName, 4e6F))

        spreadMetrics.map { (amount, metrics) ->
            plot(timeWeightedSpreadsPlot(amount, metrics.first, platformName))
        }

        plot(midPriceCandlestickPlot(spreadMetrics, platformName))
    }.makeFile()
}