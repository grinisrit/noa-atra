package com.grinisrit.crypto.binance

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import org.litote.kmongo.coroutine.CoroutineClient


class BinanceMongoDBHandler(client: CoroutineClient) : MongoDBHandler(
    client,
    PlatformName.BINANCE,
    listOf("trade", "update")
) {

    override suspend fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, BinanceDataSerializer)
        if (dataTime.platform_data is Event) {
            return
        }
        val col = nameToCollection[dataTime.platform_data.type]
        col?.insertOne(dataTime)
    }

}