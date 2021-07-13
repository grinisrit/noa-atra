package com.grinisrit.crypto.deribit

import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

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
    val amount: Double
)

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
data class UpdateData(
    val price: Double,
    val amount: Double,
)

object UpdateDataSerializer :
    JsonTransformingSerializer<UpdateData>(UpdateData.serializer()) {
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
    val bids: List<@Serializable(with = UpdateDataSerializer::class) UpdateData>,
    val asks: List<@Serializable(with = UpdateDataSerializer::class) UpdateData>
)

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

//TODO()
object DeribitDataSerializer : JsonContentPolymorphicSerializer<DeribitData>(DeribitData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element.jsonObject["params"]!!.jsonObject["channel"]!!.toString().startsWith("\"book.") -> Book.serializer()
        else -> Trades.serializer()
    }
}