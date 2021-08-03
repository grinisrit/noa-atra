package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.PlatformName
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class BitstampMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.bitstamp.toString())

    fun getOrderBook(symbol: String): Flow<TimestampedOrderBook> {
        val collection = database.getCollection<TimestampedOrderBook>(BitstampDataType.order_book.toString())
        val channel = "detail_order_book_$symbol"
        return collection.find(TimestampedOrderBook::platform_data / OrderBook::channel eq channel).toFlow()
    }
}