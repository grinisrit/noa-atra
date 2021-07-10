package com.grinisrit.crypto

import kotlinx.serialization.Serializable


interface Platform {
    val platformName: String
    val websocket_address: String
    val zeromq_address: String
    val status: String
}

@Serializable
data class CoinbasePlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
) : Platform {
    override val platformName = "coinbase"
}

@Serializable
data class BinancePlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
) : Platform {
    override val platformName = "binance"
}

@Serializable
data class DeribitPlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
) : Platform {
    override val platformName = "deribit"
}

@Serializable
data class Platforms(
    val coinbase: CoinbasePlatform?,
//    val kraken: Platform?,
    val binance: BinancePlatform?,
    val deribit: DeribitPlatform?,
)