package com.grinisrit.crypto.kraken

import com.grinisrit.crypto.common.DataTime
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection


object KrakenMongoDBHandler : MongoDBHandler {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, KrakenDataSerializer)
        if (dataTime.data.type == "event") {
            return
        }
        val col = database.getCollection<DataTime<KrakenData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }


}