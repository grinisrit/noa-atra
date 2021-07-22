package com.grinisrit.crypto

import kotlinx.cli.*
import ch.qos.logback.classic.LoggerContext

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.bitstamp.*
import com.grinisrit.crypto.coinbase.*
import com.grinisrit.crypto.common.getPubSocket
import com.grinisrit.crypto.common.getSubSocket
import com.grinisrit.crypto.common.mongodb.DBService
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.grinisrit.crypto.common.zeromq.ZeroMQSubClient
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.kraken.*
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo

import java.io.File
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {


    // TODO arg parse fun

    val cliParser = ArgParser("data")

    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()

    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"

    val config = parseConf(File(configPath).readText())

    // TODO log better
    (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level =
        ch.qos.logback.classic.Level.ERROR



    val pubSocket = getPubSocket(config.zeromq)

    val subSocket = getSubSocket(config.zeromq)


    // TODO
    val dbService = DBService()


    runBlocking {

        with(config.mongodb) {
            if (isOn) {
                val kMongoClient = KMongo.createClient(address).coroutine

                val zeroMQSubClient = ZeroMQSubClient(subSocket)

                launch(Dispatchers.IO) {
                    zeroMQSubClient.run()
                }

                // TODO() better
                dbService.apply {
                    mongoClient = kMongoClient
                    this.zeroMQSubClient = zeroMQSubClient
                }

                val client = MongoDBClient(zeroMQSubClient.getData("")).apply {
                    addHandlers(
                        BinanceMongoDBHandler(kMongoClient),
                        CoinbaseMongoDBHandler(kMongoClient),
                        DeribitMongoDBHandler(kMongoClient),
                        KrakenMongoDBHandler(kMongoClient),
                        BitstampMongoDBHandler(kMongoClient),
                    )
                }

                launch {
                    client.run()
                }
            }
        }

        with(config.platforms.binance) {
            if (isOn) {

                val request = BinanceWebsocketRequestBuilder.buildRequest(symbols).first()

                val websocketClient = BinanceWebsocketClient(
                    this,
                    pubSocket,
                    request
                )
                launch {
                    websocketClient.run()
                }


                val apiClient = BinanceAPIClient(
                    this,
                    dbService.zeroMQSubClient.getData(PlatformName.BINANCE.toString()),
                    dbService.mongoClient
                )

                launch {
                    delay(2000)
                    apiClient.run()
                }


            }
        }

        with(config.platforms.coinbase) {
            if (isOn) {

                val request = CoinbaseWebsocketRequestBuilder.buildRequest(symbols).first()

                val websocketClient = CoinbaseWebsocketClient(
                    this,
                    pubSocket,
                    request
                )

                launch {
                    websocketClient.run()
                }

            }
        }

        with(config.platforms.deribit) {
            if (isOn) {

                val request = DeribitWebsocketRequestBuilder.buildRequest(symbols).first()

                val websocketClient = DeribitWebsocketClient(
                    this,
                    pubSocket,
                    request
                )

                launch {
                    websocketClient.run()
                }


            }
        }

        with(config.platforms.kraken) {
            if (isOn) {

                val requests = KrakenWebsocketRequestBuilder.buildRequest(symbols)

                val websocketClient = KrakenWebsocketClient(
                    this,
                    pubSocket,
                    requests
                )
                launch {
                    websocketClient.run()
                }


            }
        }

        with(config.platforms.bitstamp) {
            if (isOn) {

                val requests = BitstampWebsocketRequestBuilder.buildRequest(symbols)

                val websocketClient = BitstampWebsocketClient(
                    this,
                    pubSocket,
                    requests
                )
                launch {
                    websocketClient.run()
                }


            }
        }

    }

}
