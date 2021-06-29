package com.grinisrit.crypto

import java.io.File


//TODO: provide path to conf.yaml as command line argument
fun main() {

    // plug, TODO: remove
    val confPath = "conf.yaml"

    val conf = parseConf(File(confPath).readText())

    //println(conf)

    //TODO: connect to MongoDB


    //TODO: connect to Coinbase WS feed to the
    // heartbeat, level2 and ticker channels for
    // the instruments "ETH-BTC" and "ETH-USD"
    // as in the example https://docs.pro.coinbase.com/#subscribe

    val coinBaseThread = CoinBaseThread(conf.coinbase, conf.zeromq)

    coinBaseThread.start()

    val mongoDBThread = MongoDBThread(conf.mongodb, conf.zeromq)

    mongoDBThread.start()


    //TODO: set up a pub/sub broker using kotlinx.coroutines.flow and jeromq
    // with source: the WS stream from Coinbase
    // and sink: persisting data to MongoDB
    println("Fetching data from crypto exchanges")


}
