package com.grinisrit.crypto

import kotlinx.serialization.Serializable
import java.util.*

interface CoinBaseInfo {
    val type: String
}

interface CoinBaseInfoTime : CoinBaseInfo {
    val time: String
    val date_time: Date
        get() = cbTimeToDate(time)
}

@Serializable
data class Heartbeat(
    override val type: String,
    val sequence: Long,
    val last_trade_id: Long,
    val product_id: String,
    override val time: String,
) : CoinBaseInfoTime

@Serializable
data class Ticker(
    override val type: String,
    val trade_id: Long,
    val sequence: Long,
    override val time: String,
    val product_id: String,
    val price: String,
    val side: String,
    val last_size: String,
    val best_bid: String,
    val best_ask: String,
    val open_24h: String,
    val volume_24h: String,
    val low_24h: String,
    val high_24h: String,
    val volume_30d: String,
) : CoinBaseInfoTime

@Serializable
data class Snapshot(
    override val type: String,
    val product_id: String,
    val bids: List<List<String>>,
    val asks: List<List<String>>,
) : CoinBaseInfo

@Serializable
data class L2Update(
    override val type: String,
    val product_id: String,
    override val time: String,
    val changes: List<List<String>>,
) : CoinBaseInfoTime
