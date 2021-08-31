package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.binance.BinanceMongoClient
import com.grinisrit.crypto.binance.BinanceRefinedDataPublisher
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10
    val symbol = "BTCUSDT"

    val bidAskPt = "binanceBidAsk$amount$symbol.pt"
    val timePt = "binanceTime$amount$symbol.pt"

    val mongoClient = BinanceMongoClient(config.mongodb.getMongoDBServer())
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = BinanceRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!

    saveBidAskMetric(spreadMetrics, bidAskPt, timePt)

}