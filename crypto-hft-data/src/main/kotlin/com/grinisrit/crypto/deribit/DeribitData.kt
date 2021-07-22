package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.time.Instant

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
    val amount: Double,
    val liquidation: String? = null,
    val iv: Double? = null,
    val block_trade_id: String? = null,
) {
    val datetime: Instant
        get() = Instant.ofEpochMilli(timestamp)
}

@Serializable
data class TradesParameters(
    val data: List<TradeData>,
    val channel: String,
)

@Serializable
data class Trades(
    val params: TradesParameters,
    val method: String,
    val jsonrpc: String,
) : DeribitData {
    override val type = "trades"
}

@Serializable
data class OrderData(
    val price: Double,
    val amount: Double,
)

object OrderDataSerializer :
    JsonTransformingSerializer<OrderData>(OrderData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0].toString().toDouble())
                put("amount", it[1].toString().toDouble())
            }
        }
    }
}

@Serializable
data class BookData(
    val timestamp: Long,
    val instrument_name: String,
    val change_id: Long,
    val bids: List<@Serializable(with = OrderDataSerializer::class) OrderData>,
    val asks: List<@Serializable(with = OrderDataSerializer::class) OrderData>
) {
    val datetime: Instant
        get() = Instant.ofEpochMilli(timestamp)
}

@Serializable
data class BookParameters(
    val data: BookData,
    val channel: String,
)

@Serializable
data class Book(
    val params: BookParameters,
    val method: String,
    val jsonrpc: String,
) : DeribitData {
    override val type = "book"
}

@Serializable
data class Event(
    override val type: String = "event"
) : DeribitData

//TODO()
object DeribitDataSerializer : JsonContentPolymorphicSerializer<DeribitData>(DeribitData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "params" !in element.jsonObject -> Event.serializer()
        element.jsonObject["params"]!!.jsonObject["channel"]!!.toString().startsWith("\"book.") -> Book.serializer()
        else -> Trades.serializer()
    }
}