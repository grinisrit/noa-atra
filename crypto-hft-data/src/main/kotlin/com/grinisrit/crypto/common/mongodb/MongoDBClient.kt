package com.grinisrit.crypto.common.mongodb

import com.grinisrit.crypto.common.DataTransport
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


class MongoDBClient(private val dataFlow: Flow<String>) {

    private val platformNameToHandler: MutableMap<String, MongoDBHandler> = mutableMapOf()

    fun addHandler(mongoDBHandler: MongoDBHandler){
        platformNameToHandler[mongoDBHandler.platformName.toString()] = mongoDBHandler
    }

    fun addHandlers(vararg mongoDBHandlers: MongoDBHandler){
        mongoDBHandlers.forEach { addHandler(it) }
    }

    suspend fun run() {
        dataFlow.collect {
            try {
                val platformName = DataTransport.getPlatformName(it)
                platformNameToHandler[platformName]?.handleData(it)
            } catch (e: Throwable) {
                println(e) // TODO log
            }

        }
    }

}
