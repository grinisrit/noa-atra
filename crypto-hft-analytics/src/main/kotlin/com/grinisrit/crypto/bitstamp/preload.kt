package com.grinisrit.crypto

import com.grinisrit.crypto.common.mongo.getMongoDBServer
import kotlinx.coroutines.runBlocking
import space.kscience.kmath.noa.cudaAvailable


// Make sure to add to the VM options:
// -Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/jnoa
fun main(args: Array<String>)  {

    val config = loadConf(args)

    runBlocking {
        with(config.mongodb) {
            if (isOn){
                println("CUDA found: ${cudaAvailable()}")
            } else {
                commonLogger.warn { "MongoDB not configured" }
            }

        }
    }
}