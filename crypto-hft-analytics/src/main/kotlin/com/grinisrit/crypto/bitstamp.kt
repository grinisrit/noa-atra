package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.bitstamp.BitstampMongoClient
import com.grinisrit.crypto.bitstamp.BitstampRefinedDataPublisher
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.coroutineScope


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10
    val symbol = "btcusd"

    val bidAskPt = "../cryptofed/bidask/$DATE/bitstampBidAsk${amount}BTCUSD.pt"
    val timePt = "../cryptofed/bidask/$DATE/bitstampTime${amount}BTCUSD.pt"

    val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())
    val unrefinedOrderBookFlow = mongoClient.loadOrderBooks(symbol)
    val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)

    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!

    saveBidAskMetric(spreadMetrics, bidAskPt, timePt)

}