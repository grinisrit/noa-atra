package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createCoinbaseSink() = CoinbaseMongoDBSink(this)

class CoinbaseMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.coinbase,
    listOf("ticker", "l2update", "snapshot")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<CoinbaseData>(marketDataFlow)
}
