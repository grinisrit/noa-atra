package com.grinisrit.crypto.binance

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoClient


class BinanceMongoDBHandler(client: MongoClient) : MongoDBHandler(
    client,
    PlatformName.BINANCE,
    listOf("trade", "update")
) {

    override fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, BinanceDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }

}