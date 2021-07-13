package com.grinisrit.crypto

import com.grinisrit.crypto.binance.BinanceMongoDBClient
import com.grinisrit.crypto.binance.BinanceWebsocketClient
import com.grinisrit.crypto.coinbase.CoinbaseMongoDBClient
import com.grinisrit.crypto.coinbase.CoinbaseWebsocketClient
import com.grinisrit.crypto.deribit.DeribitMongoDBClient
import com.grinisrit.crypto.deribit.DeribitWebsocketClient
import com.grinisrit.crypto.kraken.KrakenMongoDBClient
import com.grinisrit.crypto.kraken.KrakenWebsocketClient
import java.io.File



//TODO: provide path to conf.yaml as command line argument
fun main(args: Array<String>) {





    // plug, TODO: remove
    val confPath = "conf.yaml"

    val conf = parseConf(File(confPath).readText())


    //println(conf)

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

   conf.platforms.kraken?.let {
       val websocketClient = KrakenWebsocketClient(
           it,
           listOf(
               File("platforms/kraken/request_book.txt").readText(),
               File("platforms/kraken/request_trade.txt").readText(),
           )// TODO()
       )

       websocketClient.start()

       val mongoDBClient = KrakenMongoDBClient(
           it,
           conf.mongodb
       )

       mongoDBClient.start()


   }



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

        websocketClient.start()

        val mongoDBClient = DeribitMongoDBClient(
            it,
            conf.mongodb
        )

        mongoDBClient.start()

    }







    println("Fetching data from crypto exchanges")


}
