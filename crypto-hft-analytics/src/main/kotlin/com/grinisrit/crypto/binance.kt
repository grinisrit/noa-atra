package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.binance.BinanceMongoClient
import com.grinisrit.crypto.binance.BinanceRefinedDataPublisher
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList

private val defaultBinanceAmounts = mapOf(
    "ethbtc" to 100,
    "btcusdt" to 10,
    "ethusdt" to 100,
    "btceur" to 10,
    "etheur" to 100
)

suspend fun computeBinanceSpreads(symbol: String, amount: Int, mongoClient: BinanceMongoClient) {
    val snapshotsList = mongoClient.loadSnapshots(symbol).toList()
    val updatesFlow = mongoClient.loadUpdates(symbol)
    val orderBookFlow = BinanceRefinedDataPublisher.orderBookFlow(snapshotsList, updatesFlow)
    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!
    val spreadsPt = "binance_${amount}${symbol}_spreads.pt"

    saveSpreads(spreadMetrics, spreadsPt)
}


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)
    val mongoClient = BinanceMongoClient(config.mongodb.getMongoDBServer())

    with(config.platforms.binance) {
        symbols.forEach { symbol ->
            defaultBinanceAmounts[symbol]?.let {
                computeBinanceSpreads(symbol, it, mongoClient)
            }
        }
    }

}