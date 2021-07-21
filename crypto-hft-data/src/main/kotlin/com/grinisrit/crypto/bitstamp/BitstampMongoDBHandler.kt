package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoClient


class BitstampMongoDBHandler(client: MongoClient) : MongoDBHandler(
    client,
    PlatformName.BITSTAMP,
    listOf("order_book", "trade")
) {

    override fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, BitstampDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }

}