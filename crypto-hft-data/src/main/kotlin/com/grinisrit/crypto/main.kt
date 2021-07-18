package com.grinisrit.crypto

import kotlinx.cli.*
import ch.qos.logback.classic.LoggerContext

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.coinbase.*
import com.grinisrit.crypto.common.getPubSocket
import com.grinisrit.crypto.common.getSubSocket
import com.grinisrit.crypto.common.mongodb.MongoDBClient
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.kraken.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import java.io.File
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread


fun main(args: Array<String>) {

    val cliParser = ArgParser("data")

    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()

    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"

    val config = parseConf(File(configPath).readText())

    // TODO something better
    (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level =
        ch.qos.logback.classic.Level.ERROR

    val subSocket = getSubSocket(config.zeromq)

    val pubSocket = getPubSocket(config.zeromq)

    val mongoIsOn = config.mongodb.status == "on"


    val client = MongoDBClient(subSocket, config.mongodb).apply {
        platformNameToHandler.putAll(
            mapOf(
                "binance" to BinanceMongoDBHandler,
                "coinbase" to CoinbaseMongoDBHandler,
                "deribit" to DeribitMongoDBHandler,
                "kraken" to KrakenMongoDBHandler,
            )
        )
    }


   client.start()

runBlocking {
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
}
}
