package com.grinisrit.crypto.binance

import com.grinisrit.crypto.common.MarkedDataFlow
import com.grinisrit.crypto.common.MongoDBServer
import com.grinisrit.crypto.common.MongoDBSink
import com.grinisrit.crypto.common.PlatformName


fun MongoDBServer.createBinanceSink() = BinanceMongoDBSink(this)

class BinanceMongoDBSink internal constructor(server: MongoDBServer) : MongoDBSink(
    server,
    PlatformName.binance,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun consume(marketDataFlow: MarkedDataFlow) =
        handleFlow<BinanceData>(marketDataFlow)
}