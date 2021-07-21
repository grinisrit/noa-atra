package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.PlatformName
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoClient

class DeribitMongoDBHandler(client: MongoClient) : MongoDBHandler(
    client,
    PlatformName.DERIBIT,
    listOf("book", "trades")
){
    override fun handleData(data: String) {
        val dataTime = DataTransport.fromDataString(data, DeribitDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = nameToCollection[dataTime.data.type]
        col?.insertOne(dataTime)
    }
}