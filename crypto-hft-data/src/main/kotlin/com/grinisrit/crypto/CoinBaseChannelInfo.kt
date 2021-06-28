package com.grinisrit.crypto

import kotlinx.serialization.Serializable

interface CoinBaseChannelInfo {
    val type: String
}
@Serializable
data class Heartbeat(
    override val type: String,
    val sequence: String,
    val last_trade_id: String,
    val product_id: String,
    val time: String,
): CoinBaseChannelInfo

