package com.grinisrit.crypto.coinbase

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.reflect.KClass

@TypeFor(field = "type", adapter = CoinBaseDataTypeAdapter::class)
interface CoinbaseData : ChannelData {
    val type: String
}

class CoinBaseDataTypeAdapter: TypeAdapter<CoinbaseData> {
    override fun classFor(type: Any): KClass<out CoinbaseData> = when(type as String) {
        "heartbeat" -> Heartbeat::class
        "ticker" -> Ticker::class
        "snapshot" -> Snapshot::class
        "l2update" -> L2Update::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
}

interface CoinbaseDataTime : CoinbaseData {
    val time: String
    val datetime: Instant
        get() = Instant.parse(time)
}

@Serializable
data class Heartbeat(
    override val type: String,
    val sequence: Long,
    val last_trade_id: Long,
    val product_id: String,
    override val time: String,
) : CoinbaseDataTime

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
) : CoinbaseDataTime

@Serializable
data class Snapshot(
    override val type: String,
    val product_id: String,
    val bids: List<List<String>>,
    val asks: List<List<String>>,
) : CoinbaseData

@Serializable
data class L2Update(
    override val type: String,
    val product_id: String,
    override val time: String,
    val changes: List<List<String>>,
) : CoinbaseDataTime
