package com.grinisrit.crypto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Platform {

    @SerialName("websocket_address")
    val websocketAddress: String
    val status: String
    val symbols: List<String>

    val platformName: String

    val isOn: Boolean
        get() = status == "on"
}

@Serializable
data class CoinbasePlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName = "coinbase"
}

@Serializable
data class BinancePlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName = "binance"
}

@Serializable
data class DeribitPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>
) : Platform {
    override val platformName = "deribit"
}

@Serializable
data class KrakenPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
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