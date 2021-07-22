package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.common.zeromq.ZeroMQSubClient
import org.litote.kmongo.coroutine.CoroutineClient

// TODO
class DBService {
    lateinit var mongoClient: CoroutineClient
    lateinit var zeroMQSubClient: ZeroMQSubClient
}
