package com.grinisrit.crypto

import kotlinx.serialization.Serializable

// TODO checks mb
interface Platform {
    val platformName: String
    val websocket_address: String
    val zeromq_address: String
    val status: String
    val symbols: List<String>

    val isOn: Boolean
        get() = status == "on"
}

@Serializable
data class CoinbasePlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName = "coinbase"
}

@Serializable
data class BinancePlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName = "binance"
}

@Serializable
data class DeribitPlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
    override val symbols: List<String>
) : Platform {
    override val platformName = "deribit"
}

@Serializable
data class KrakenPlatform(
    override val websocket_address: String,
    override val zeromq_address: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName = "kraken"
}

@Serializable
data class Platforms(
    val coinbase: CoinbasePlatform,
    val kraken: KrakenPlatform,
    val binance: BinancePlatform,
    val deribit: DeribitPlatform,
)