package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.Serializable

interface DeribitData : ChannelData {
    val type: String
}

@Serializable
data class TradeData(
    val trade_seq: Long,
    val trade_id: String,
    val timestamp: Long,
    val tick_direction: Int,
    val price: Double,
    val mark_price: Double,
    val instrument_name: String,
    val index_price: Double,
    val direction: String,
    val amount: Int
)

@Serializable
data class Parameters(
    val data: List<TradeData>,
    val channel: String,
)

@Serializable
data class Trades(
    val params: Parameters,
    val method: String,
    val jsonrpc: String,
) : DeribitData {
    override val type = "trades"
}