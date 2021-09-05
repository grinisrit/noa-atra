package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.kraken.KrakenMongoClient
import com.grinisrit.crypto.kraken.KrakenRefinedDataPublisher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList


private val defaultKrakenAmounts = mapOf(
    "ETH/USD" to 100,
    "ETH/XBT" to 100,
    "XBT/USD" to 10,
    "XBT/EUR" to 10,
    "ETH/EUR" to 100
)

suspend fun computeKrakenSpreads(symbol: String, amount: Int, mongoClient: KrakenMongoClient) {
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = KrakenRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!
    val spreadsPt = "kraken_${amount}${symbol.replace("/", "-")}_spreads.pt"

    saveSpreads(spreadMetrics, spreadsPt)
}


suspend fun saveKrakenSpreads(config: ConfYAMl) = coroutineScope {
    val mongoClient = KrakenMongoClient(config.mongodb.getMongoDBServer())
    with(config.platforms.kraken) {
        symbols.forEach { symbol ->
            defaultKrakenAmounts[symbol]?.let {
                computeKrakenSpreads(symbol, it, mongoClient)
            }
        }
    }
}

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)
    saveKrakenSpreads(config)
}

