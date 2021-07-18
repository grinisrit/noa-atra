package com.grinisrit.crypto.common.mongodb

import com.mongodb.client.MongoDatabase

interface MongoDBHandler {
    fun handleData(data: String, database: MongoDatabase)
}