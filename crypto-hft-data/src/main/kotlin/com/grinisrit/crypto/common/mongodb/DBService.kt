package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.common.zeromq.ZeroMQSubClient
import com.mongodb.client.MongoClient

// TODO
class DBService {
    lateinit var mongoClient: MongoClient
    lateinit var zeroMQSubClient: ZeroMQSubClient
}
