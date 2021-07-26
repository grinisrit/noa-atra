package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.TimestampedDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName

fun MongoDBServer.createCoinbaseSink() = CoinbaseMongoDBSink(this)

class CoinbaseMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.coinbase,
    CoinbaseDataType.values()
) {
    override suspend fun consume(marketDataFlow: TimestampedDataFlow) =
        handleFlow<CoinbaseData>(marketDataFlow)
}
