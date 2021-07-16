package com.grinisrit.crypto

import kotlinx.cli.*
import ch.qos.logback.classic.LoggerContext

import com.grinisrit.crypto.binance.*
import com.grinisrit.crypto.coinbase.*
import com.grinisrit.crypto.deribit.*
import com.grinisrit.crypto.kraken.*

import java.io.File
import org.slf4j.LoggerFactory


fun main(args: Array<String>) {

    val cliParser = ArgParser("data")

    val configPathArg by cliParser.argument(ArgType.String, description = "Path to .yaml config file").optional()

    cliParser.parse(args)

    val configPath = configPathArg ?: "conf.yaml"

    val config = parseConf(File(configPath).readText())

    // TODO something better
    (LoggerFactory.getILoggerFactory() as LoggerContext).getLogger("org.mongodb.driver").level =
        ch.qos.logback.classic.Level.ERROR

    val mongoIsOn = config.mongodb.status == "on"

    with(config.platforms.binance) {
        if (isOn) {

            val request = BinanceWebsocketRequestBuilder.buildRequest(symbols).first()

            val websocketClient = BinanceWebsocketClient(
                this,
                request
            )

            websocketClient.start()

            if (mongoIsOn){
                val mongoDBClient = BinanceMongoDBClient(
                    this,
                    config.mongodb
                )
                mongoDBClient.start()
            }

        }
    }

    with(config.platforms.coinbase) {
        if (isOn) {

            val request = CoinbaseWebsocketRequestBuilder.buildRequest(symbols).first()

            val websocketClient = CoinbaseWebsocketClient(
                this,
                request
            )

            websocketClient.start()

            if (mongoIsOn) {
                val mongoDBClient = CoinbaseMongoDBClient(
                    this,
                    config.mongodb
                )
                mongoDBClient.start()
            }

        }
    }

    with(config.platforms.deribit) {
        if (isOn) {

            val request = DeribitWebsocketRequestBuilder.buildRequest(symbols).first()

            val websocketClient = DeribitWebsocketClient(
                this,
                request
            )

            websocketClient.start()

            if (mongoIsOn) {
                val mongoDBClient = DeribitMongoDBClient(
                    this,
                    config.mongodb
                )
                mongoDBClient.start()
            }

        }
    }

    with(config.platforms.kraken) {
        if (isOn) {

            val requests = KrakenWebsocketRequestBuilder.buildRequest(symbols)

            val websocketClient = KrakenWebsocketClient(
                this,
                requests
            )

            websocketClient.start()

            if (mongoIsOn) {
                val mongoDBClient = KrakenMongoDBClient(
                    this,
                    config.mongodb
                )
                mongoDBClient.start()
            }

        }
    }

}
