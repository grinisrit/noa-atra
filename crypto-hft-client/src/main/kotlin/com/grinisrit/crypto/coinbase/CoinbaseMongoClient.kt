package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.common.*
import com.grinisrit.crypto.common.mongo.MongoDBServer
import kotlinx.coroutines.flow.*
import org.litote.kmongo.*

class CoinbaseMongoClient(server: MongoDBServer) {
    private val database = server.client.getDatabase(PlatformName.coinbase.toString())

    fun loadSnapshots(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedSnapshot>(CoinbaseDataType.snapshot.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / CoinbaseSnapshot::product_id eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadUpdates(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedL2Update>(CoinbaseDataType.l2update.toString())
        return collection.find(
            TimestampedSnapshot::platform_data / CoinbaseL2Update::product_id eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

    fun loadTrades(symbol: String): unrefinedDataFlow {
        val collection = database.getCollection<TimestampedTrade>(CoinbaseDataType.match.toString())
        return collection.find(
            TimestampedTrade::platform_data / CoinbaseMatch::product_id eq symbol
        ).toFlow().map {
            it.toTimestampedData()
        }
    }

}