package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.finery.FineryMongoClient
import com.grinisrit.crypto.finery.FineryRefinedDataPublisher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)

    val amount = 10
    val symbol = "BTC-USD"

    val bidAskPt = "../cryptofed/bidask/$DATE/fineryBidAsk${amount}BTCUSD.pt"
    val timePt = "../cryptofed/bidask/$DATE/fineryTime${amount}BTCUSD.pt"

    val mongoClient = FineryMongoClient(config.mongodb.getMongoDBServer())
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = FineryRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!

    saveBidAskMetric(spreadMetrics, bidAskPt, timePt)

}