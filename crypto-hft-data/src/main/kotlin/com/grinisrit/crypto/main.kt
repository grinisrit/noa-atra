package com.grinisrit.crypto

import com.grinisrit.crypto.coinbase.CoinbaseMongoDBClient
import com.grinisrit.crypto.coinbase.CoinbaseWebsocketClient
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.io.File


//TODO: provide path to conf.yaml as command line argument
fun main() {


    // plug, TODO: remove
    val confPath = "conf.yaml"

    val conf = parseConf(File(confPath).readText())

    //TODO: connect to MongoDB


    //TODO: connect to Coinbase WS feed to the
    // heartbeat, level2 and ticker channels for
    // the instruments "ETH-BTC" and "ETH-USD"
    // as in the example https://docs.pro.coinbase.com/#subscribe
    val context = ZContext()
    val socket = context.createSocket(SocketType.PUB)
    val zeroMQAddress = "tcp://${conf.zeromq.address}:${conf.zeromq.port}"
    socket.bind(zeroMQAddress)

    val client = CoinbaseWebsocketClient(
        conf.coinbase.address,
        socket,
        File("platforms/coinbase/request.txt").readText()
    )

    client.start()


    val mongoCoinbaseClient = CoinbaseMongoDBClient(conf.mongodb, conf.zeromq)

    mongoCoinbaseClient.start()









    //TODO: set up a pub/sub broker using kotlinx.coroutines.flow and jeromq
    // with source: the WS stream from Coinbase
    // and sink: persisting data to MongoDB
    println("Fetching data from crypto exchanges")


}
