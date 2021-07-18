package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.MongoDB
import com.grinisrit.crypto.Platform
import com.grinisrit.crypto.common.DataTransport
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.litote.kmongo.KMongo
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ


class MongoDBClient(private val socketSUB: ZMQ.Socket, mongoDB: MongoDB): Thread() {

    val client = KMongo.createClient(mongoDB.address)

    private fun subscriptionFlow() = flow {
        socketSUB.subscribe("")
        while (true) {
            val data = socketSUB.recvStr() ?: continue
            emit(data)
        }
    }.flowOn(Dispatchers.IO)

    val platformNameToHandler: MutableMap<String, MongoDBHandler> = mutableMapOf()

    override fun run() {
            subscriptionFlow().onEach {
                    val platformName = DataTransport.getPlatformName(it)
                    val database = client.getDatabase(platformName)
                    platformNameToHandler[platformName]?.handleData(it, database)
                        ?: throw Error("Unknown platform $platformName")

            }.launchIn(GlobalScope)

    }

}
