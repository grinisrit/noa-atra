package com.grinisrit.crypto.bitstamp

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import com.grinisrit.crypto.commonLogger
import com.grinisrit.crypto.loadConf
import kotlinx.coroutines.runBlocking


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
fun main(args: Array<String>)  {

    val config = loadConf(args)

    runBlocking {
        with(config.mongodb) {
            if (isOn){
                val mongo = getMongoDBServer()
                val binanceDB = mongo.createBitstampSink()
                val orderBook = binanceDB.getCollection(BitstampDataType.order_book)

            } else {
                commonLogger.warn { "MongoDB not configured" }
            }

        }
    }
}