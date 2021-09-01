package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.coinbase.CoinbaseMongoClient
import com.grinisrit.crypto.coinbase.CoinbaseRefinedDataPublisher
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList


private val defaultCoinbaseAmounts = mapOf(
    "ETH-BTC" to 100,
    "BTC-USD" to 10,
    "ETH-USD" to 100,
    "BTC-EUR" to 10,
    "ETH-EUR" to 100
)

suspend fun computeCoinbaseSpreads(symbol: String, amount: Int, mongoClient: CoinbaseMongoClient) {
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = CoinbaseRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!
    val spreadsPt = "coinbase_${amount}${symbol}_spreads.pt"

    saveSpreads(spreadMetrics, spreadsPt)
}

// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {
    val config = loadConf(args)
    val mongoClient = CoinbaseMongoClient(config.mongodb.getMongoDBServer())

    with(config.platforms.coinbase) {
        symbols.forEach { symbol ->
            defaultCoinbaseAmounts[symbol]?.let {
                computeCoinbaseSpreads(symbol, it, mongoClient)
            }
        }
    }

}
