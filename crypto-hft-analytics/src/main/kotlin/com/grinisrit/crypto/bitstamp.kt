package com.grinisrit.crypto

import com.grinisrit.crypto.analysis.countTimeWeightedMetricsAndLiquidity
import com.grinisrit.crypto.bitstamp.BitstampMongoClient
import com.grinisrit.crypto.bitstamp.BitstampRefinedDataPublisher
import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.coroutineScope


private val defaultBitstampAmounts = mapOf(
    "ethbtc" to 100,
    "btcusd" to 10,
    "ethusd" to 100,
    "btceur" to 10,
    "etheur" to 100
)


suspend fun computeBitstampSpreads(symbol: String, amount: Int, mongoClient: BitstampMongoClient) {
    val unrefinedOrderBookFlow = mongoClient.loadOrderBooks(symbol)
    val orderBookFlow = BitstampRefinedDataPublisher.orderBookFlow(unrefinedOrderBookFlow)

    val spreadMetrics = countTimeWeightedMetricsAndLiquidity(orderBookFlow, listOf(amount))[amount]!!
    val spreadsPt = "bitstamp_${amount}${symbol}_spreads.pt"

    saveSpreads(spreadMetrics, spreadsPt)
}


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
suspend fun main(args: Array<String>) = coroutineScope {

    val config = loadConf(args)
    val mongoClient = BitstampMongoClient(config.mongodb.getMongoDBServer())

    with(config.platforms.bitstamp) {
        symbols.forEach { symbol ->
            defaultBitstampAmounts[symbol]?.let {
                computeBitstampSpreads(symbol, it, mongoClient)
            }
        }
    }

}