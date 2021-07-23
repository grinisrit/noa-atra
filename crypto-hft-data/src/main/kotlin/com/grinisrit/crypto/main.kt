package com.grinisrit.crypto

import kotlinx.cli.*

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.bitstamp.*
import com.grinisrit.crypto.coinbase.*
import com.grinisrit.crypto.common.getPubSocket
import com.grinisrit.crypto.common.getSubSocket
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.grinisrit.crypto.common.zeromq.ZeroMQSubClient
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.kraken.*
import kotlinx.coroutines.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo

import java.io.File
import mu.KotlinLogging
import org.zeromq.ZContext


internal val logger = KotlinLogging.logger { }


fun main(args: Array<String>) {

    val cliParser = ArgParser("data")

    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()

    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"

    val config = parseConf(File(configPath).readText())


    val zmqContext = ZContext()

    val pubSocket = zmqContext.getPubSocket(config.zeromq)

    val subSocket = zmqContext.getSubSocket(config.zeromq)



    runBlocking {

        with(config.mongodb) {
            if (isOn) {
                val kMongoClient = KMongo.createClient(address).coroutine

                val zeroMQSubClient = ZeroMQSubClient(subSocket)

                launch(Dispatchers.IO) {
                    zeroMQSubClient.run()
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

                // TODO Anything better?????
                with(config.platforms.binance) {
                    if (isOn) {
                        val apiClient = BinanceAPIClient(
                            this,
                            zeroMQSubClient.getData(PlatformName.BINANCE.toString()),
                            kMongoClient
                        )

                        launch {
                            delay(2000)
                            apiClient.run()
                        }
                    }
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

    zmqContext.close()

}
