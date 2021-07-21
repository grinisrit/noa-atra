package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.DataTime
import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

object BitstampMongoDBHandler : MongoDBHandler {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, BitstampDataSerializer)
        if (dataTime.data is Event) {
            return
        }
        val col = database.getCollection<DataTime<BitstampData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }

}