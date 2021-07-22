package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoClient

class CoinbaseMongoDBHandler(client: MongoClient) : MongoDBHandler(
    client,
    PlatformName.COINBASE,
    listOf("ticker", "l2update", "snapshot")
){
    override fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, CoinbaseDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }
}
