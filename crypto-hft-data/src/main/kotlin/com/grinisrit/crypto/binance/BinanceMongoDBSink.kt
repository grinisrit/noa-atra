package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.TimestampedDataFlow
import com.grinisrit.crypto.common.mongo.MongoDBServer
import com.grinisrit.crypto.common.mongo.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createBinanceSink() = BinanceMongoDBSink(this)

class BinanceMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.binance,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun consume(marketDataFlow: TimestampedDataFlow) =
        handleFlow<BinanceData>(marketDataFlow)
}