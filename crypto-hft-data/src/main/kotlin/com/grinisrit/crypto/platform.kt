package com.grinisrit.crypto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Platform {

    @SerialName("websocket_address")
    val websocketAddress: String
    val status: String
    val symbols: List<String>
    val platformName: PlatformName

    val name: String
        get() = platformName.toString()

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
    override val platformName: PlatformName = PlatformName.COINBASE
}

@Serializable
data class BinancePlatform(
    @SerialName("api_address")
    val apiAddress: String,
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.BINANCE
}

@Serializable
data class DeribitPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>
) : Platform {
    override val platformName: PlatformName = PlatformName.DERIBIT
}

@Serializable
data class KrakenPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.KRAKEN
}

@Serializable
data class BitstampPlatform(
    @SerialName("websocket_address")
    override val websocketAddress: String,
    override val status: String,
    override val symbols: List<String>,
) : Platform {
    override val platformName: PlatformName = PlatformName.BITSTAMP
}

@Serializable
data class Platforms(
    val coinbase: CoinbasePlatform,
    val kraken: KrakenPlatform,
    val binance: BinancePlatform,
    val deribit: DeribitPlatform,
    val bitstamp: BitstampPlatform,
)