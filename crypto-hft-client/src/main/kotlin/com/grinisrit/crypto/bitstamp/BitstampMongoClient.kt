package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class BitstampMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.bitstamp.toString())

    fun loadOrderBooks(symbol: String): RawDataFlow {
        val collection = database.getCollection<TimestampedOrderBook>(BitstampDataType.order_book.toString())
        val channel = "detail_order_book_$symbol"
        return collection.find(
            TimestampedOrderBook::platform_data / BitstampOrderBook::channel eq channel
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadTrades(symbol: String): RawDataFlow {
        val collection = database.getCollection<TimestampedTrade>(BitstampDataType.trade.toString())
        val channel = "live_trades_$symbol"
        return collection.find(
            TimestampedTrade::platform_data / BitstampTrade::channel eq channel
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

}