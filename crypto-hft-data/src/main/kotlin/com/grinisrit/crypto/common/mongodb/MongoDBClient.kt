package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.common.DataTransport
import kotlinx.coroutines.*
import org.litote.kmongo.KMongo
import org.zeromq.ZMQ


class MongoDBClient(private val socketSUB: ZMQ.Socket) {

    val platformNameToHandler: MutableMap<String, MongoDBHandler> = mutableMapOf()

    fun addHandler(mongoDBHandler: MongoDBHandler){
        platformNameToHandler[mongoDBHandler.platformName.toString()] = mongoDBHandler
    }

    fun addHandlers(vararg mongoDBHandlers: MongoDBHandler){
        mongoDBHandlers.forEach { addHandler(it) }
    }

    fun run(coroutineScope: CoroutineScope?) {
        while (coroutineScope?.isActive != false) {
            val data = socketSUB.recvStr() ?: continue
            val platformName = DataTransport.getPlatformName(data)
            try {
                platformNameToHandler[platformName]?.handleData(data)
            } catch (e: Throwable) {
                // todo log
                println(e)
            }
        }
    }

}
