package com.grinisrit.crypto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.grinisrit.crypto.binance.BinanceMongoDBClient
import com.grinisrit.crypto.binance.BinanceWebsocketClient
import com.grinisrit.crypto.coinbase.CoinbaseMongoDBClient
import com.grinisrit.crypto.coinbase.CoinbaseWebsocketClient
import com.grinisrit.crypto.deribit.DeribitWebsocketClient
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import org.zeromq.SocketType
import org.zeromq.ZContext
import java.io.File



//TODO: provide path to conf.yaml as command line argument
fun main() {

/*
    // plug, TODO: remove
    val confPath = "conf.yaml"

    val conf = parseConf(File(confPath).readText())

    println(conf)

   conf.platforms.coinbase?.let {
       val websocketClient = CoinbaseWebsocketClient(
           it,
           File("platforms/coinbase/request.txt").readText() // TODO()
       )

       websocketClient.start()

       val mongoDBClient = CoinbaseMongoDBClient(
           it,
           conf.mongodb
       )

       mongoDBClient.start()
   }

 */
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

     */

/*
    conf.platforms.deribit?.let {
        val websocketClient = DeribitWebsocketClient(
            it,
            File("platforms/deribit/request.txt").readText() // TODO()
        )

        websocketClient.start()

        val mongoDBClient = BinanceMongoDBClient(
            it,
            conf.mongodb
        )



        mongoDBClient.start()


    } */







    println("Fetching data from crypto exchanges")


}
