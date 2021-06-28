package com.grinisrit.crypto

//TODO: Andrei

//TODO: provide path to conf.yaml as command line argument
fun main(){
    //TODO: read and parse configuration from conf.yaml

    //TODO: connect to MongoDB

    //TODO: connect to Coinbase WS feed to the
    // heartbeat, level2 and ticker channels for
    // the instruments "ETH-BTC" and "ETH-USD"
    // as in the example https://docs.pro.coinbase.com/#subscribe

    //TODO: set up a pub/sub broker using kotlinx.coroutines.flow and jeromq
    // with source: the WS stream from Coinbase
    // and sink: persisting data to MongoDB
    println("Fetching data from crypto exchanges")
}