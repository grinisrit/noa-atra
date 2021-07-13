package com.grinisrit.crypto

import com.google.gson.*
import com.grinisrit.crypto.kraken.KrakenMongoDBClient
import com.grinisrit.crypto.kraken.KrakenWebsocketClient
import io.ktor.utils.io.*
import java.io.File


//TODO: provide path to conf.yaml as command line argument
fun main() {





    // plug, TODO: remove
    val confPath = "conf.yaml"

    val conf = parseConf(File(confPath).readText())

    println(conf)

   conf.platforms.kraken?.let {
       val websocketClient = KrakenWebsocketClient(
           it,
           File("platforms/kraken/request.txt").readText() // TODO()
       )

       websocketClient.start()

       val mongoDBClient = KrakenMongoDBClient(
           it,
           conf.mongodb
       )

       mongoDBClient.start()


   }


    /*
    conf.platforms.binance?.let {
        val websocketClient = BinanceWebsocketClient(
            it,
            File("platforms/binance/request.txt").readText() // TODO()
        )

        websocketClient.start()

        val mongoDBClient = BinanceMongoDBClient(
            it,
            conf.mongodb
        )

        mongoDBClient.start()


    }




    conf.platforms.deribit?.let {
        val websocketClient = DeribitWebsocketClient(
            it,
            File("platforms/deribit/request.txt").readText() // TODO()
        )

        websocketClient.start() */
/*
        val mongoDBClient = BinanceMongoDBClient(
            it,
            conf.mongodb
        )





        mongoDBClient.start()


    }







    println("Fetching data from crypto exchanges")

    */
}
