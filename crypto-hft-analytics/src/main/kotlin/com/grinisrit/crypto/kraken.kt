package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.kraken.KrakenMongoClient
import com.grinisrit.crypto.kraken.KrakenRefinedDataPublisher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)

    val amount = 10
    val symbol = "XBT/USD"

    val bidAskPt = "krakenBidAsk$amount${symbol.replace("/", "")}.pt"
    val timePt = "krakenTime$amount${symbol.replace("/", "")}.pt"

    val mongoClient = KrakenMongoClient(config.mongodb.getMongoDBServer())
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = KrakenRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]?.first!!

    saveBidAskMetric(spreadMetrics, bidAskPt, timePt)
}
