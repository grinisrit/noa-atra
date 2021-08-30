package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.loadConf
import com.grinisrit.crypto.saveBidAskMetric
import kotlinx.coroutines.coroutineScope


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10
    val symbol = "btcusd"

    val bidAskPt = "bitstampBidAsk$amount$symbol.pt"
    val timePt = "bitstampTime$amount$symbol.pt"

    val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
    val unrefinedOrderBookFlow = mongoClient.loadOrderBooks(symbol)
    val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)

    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]?.first!!

    saveBidAskMetric(spreadMetrics, bidAskPt, timePt)

}