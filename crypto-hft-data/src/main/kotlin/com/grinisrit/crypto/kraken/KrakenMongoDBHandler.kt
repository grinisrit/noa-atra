package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoClient

class KrakenMongoDBHandler(client: MongoClient) : MongoDBHandler(
    client,
    PlatformName.KRAKEN,
    listOf("snapshot", "trade", "update")
) {
    override fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, KrakenDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }
}