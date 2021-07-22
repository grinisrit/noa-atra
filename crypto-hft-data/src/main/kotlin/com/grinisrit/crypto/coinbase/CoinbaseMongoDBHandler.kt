package com.grinisrit.crypto.coinbase

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import org.litote.kmongo.coroutine.CoroutineClient

class CoinbaseMongoDBHandler(client: CoroutineClient) : MongoDBHandler(
    client,
    PlatformName.COINBASE,
    listOf("ticker", "l2update", "snapshot")
){
    override suspend fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, CoinbaseDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }
}
