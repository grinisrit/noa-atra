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
) : CoinBaseChannelInfo

@Serializable
data class Ticker(
    override val type: String,
    val trade_id: String,
    val sequence: String,
    val time: String,
    val product_id: String,
    val price: String,
    val side: String,
    val last_size: String,
    val best_bid: String,
    val best_ask: String,
) : CoinBaseChannelInfo

@Serializable
data class Snapshot(
    override val type: String,
    val product_id: String,
    val bids: List<List<String>>,
    val asks: List<List<String>>,
) : CoinBaseChannelInfo

@Serializable
data class L2Update(
    override val type: String,
    val product_id: String,
    val time: String,
    val changes: List<List<String>>,
) : CoinBaseChannelInfo