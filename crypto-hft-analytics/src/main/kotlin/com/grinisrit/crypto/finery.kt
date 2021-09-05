package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.finery.FineryMongoClient
import com.grinisrit.crypto.finery.FineryRefinedDataPublisher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

private val defaultFineryAmounts = mapOf(
    "ETH-BTC" to 100,
    "BTC-USD" to 10,
    "ETH-USD" to 100,
    "BTC-EUR" to 10,
    "ETH-EUR" to 100
)

suspend fun computeFinerySpreads(symbol: String, amount: Int, mongoClient: FineryMongoClient) {
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = FineryRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!
    val spreadsPt = "finery_${amount}${symbol}_spreads.pt"

    saveSpreads(spreadMetrics, spreadsPt)
}

suspend fun saveFinerySpreads(config: ConfYAMl) = coroutineScope {
    val mongoClient = FineryMongoClient(config.mongodb.getMongoDBServer())

    with(config.platforms.finery) {
        symbols.forEach { symbol ->
            defaultFineryAmounts[symbol]?.let {
                computeFinerySpreads(symbol, it, mongoClient)
            }
        }
    }

}

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)
    saveFinerySpreads(config)
}