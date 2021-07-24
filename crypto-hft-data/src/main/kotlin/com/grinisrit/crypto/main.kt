package com.grinisrit.crypto

import kotlinx.cli.*
import kotlinx.coroutines.*
import java.io.File
import mu.KotlinLogging

internal val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {

    val cliParser = ArgParser("data")
    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()
    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"
    val config = parseConf(File(configPath).readText())



    runBlocking {
/*
        with(config.mongodb) {
            if (isOn) {

                val mongoServer = getMongoDBServer(this)


                val kMongoClient = try {
                    KMongo.createClient(address).coroutine
                } catch (e: Throwable) {
                    logger.error(e) { "Failed to connect to mongo" }
                    throw RuntimeException("FUCK")
                }

               // val zeroMQSubClient = ZeroMQSubClient(subSocket)


                val dataFlow = zmqSubFlow(subSocket, Dispatchers.IO)
                /*
                launch(Dispatchers.IO) {
                    zeroMQSubClient.run()
                }

                 */

                val client = MongoDBClient(dataFlow).apply {
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
                            dataFlow.filter { it.startsWith(PlatformName.BINANCE.toString()) },
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
        }*/

    }

}
