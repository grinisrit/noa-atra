package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import org.litote.kmongo.coroutine.CoroutineClient

class KrakenMongoDBHandler(client: CoroutineClient) : MongoDBHandler(
    client,
    PlatformName.KRAKEN,
    listOf("snapshot", "trade", "update")
) {
    override suspend fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, KrakenDataSerializer)
        if (dataTime.platform_data is Event) {
            return
        }
        val col = nameToCollection[dataTime.platform_data.type]
        col?.insertOne(dataTime)
    }
}