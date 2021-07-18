package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.DataTransport
import com.grinisrit.crypto.common.mongodb.MongoDBHandler
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.getCollection

object DeribitMongoDBHandler : MongoDBHandler {
    override fun handleData(data: String, database: MongoDatabase) {
        val dataTime = DataTransport.fromDataString(data, DeribitDataSerializer)
        if (dataTime.data is Event){
            return
        }
        val col = database.getCollection<DataTransport.DataTime<DeribitData>>(dataTime.data.type)
        col.insertOne(dataTime)
    }
}