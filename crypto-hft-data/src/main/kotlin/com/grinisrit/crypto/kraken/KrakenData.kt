package com.grinisrit.crypto.kraken


import com.grinisrit.crypto.common.ChannelData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

interface KrakenData : ChannelData {
    val type: String
}

@Serializable
class Event : KrakenData {
    override val type: String = "event"
}

@Serializable
data class TradeData(
    val price: String,
    val volume: String,
    val time: String,
    val side: String,
    val orderType: String,
    val misc: String,
)

object TradeDataSerializer :
    JsonTransformingSerializer<TradeData>(TradeData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("volume", it[1])
                put("time", it[2])
                put("side", it[3])
                put("orderType", it[4])
                put("misc", it[5])
            }
        }
    }
}

@Serializable
data class Trade(
    val channelId: Int,
    val tradeData: List<@Serializable(with = TradeDataSerializer::class) TradeData>,
    val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "trade"
}

object TradeSerializer :
    JsonTransformingSerializer<Trade>(Trade.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("channelId", it[0].toString().toInt())
                put("tradeData", it[1])
                put("channelName", it[2])
                put("pair", it[3])
            }
        }
    }
}

@Serializable
data class OrderData(
    val price: String,
    val volume: String,
    val timestamp: String,
)

object OrderDataSerializer :
    JsonTransformingSerializer<OrderData>(OrderData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("volume", it[1])
                put("timestamp", it[2])
            }
        }
    }
}

@Serializable
data class BookSnapshotData(
    @SerialName("as") val asks: List<@Serializable(with = OrderDataSerializer::class)OrderData>,
    @SerialName("bs") val bids: List<@Serializable(with = OrderDataSerializer::class)OrderData>,
)

@Serializable
data class BookSnapshot(
    val channelId: Int,
    val bookData: BookSnapshotData,
    val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "snapshot"
}

object BookSnapshotSerializer :
    JsonTransformingSerializer<BookSnapshot>(BookSnapshot.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("channelId", it[0].toString().toInt())
                put("bookData", it[1])
                put("channelName", it[2])
                put("pair", it[3])
            }
        }
    }
}

@Serializable
data class UpdateData(
    val price: String,
    val volume: String,
    val timestamp: String,
    val updateType: String? = null,
)

object UpdateDataSerializer :
    JsonTransformingSerializer<UpdateData>(UpdateData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("volume", it[1])
                put("timestamp", it[2])
                if (it.size > 3) {
                    put("updateType", it[3])
                }
            }
        }
    }
}

@Serializable
data class AsksUpdate(
    val a: List<@Serializable(with = UpdateDataSerializer::class)UpdateData>,
    val c: String? = null,
)

@Serializable
data class BidsUpdate(
    val b: List<@Serializable(with = UpdateDataSerializer::class)UpdateData>,
    val c: String? = null,
)

@Serializable
data class BookUpdate(
    val channelId: Int,
    val asksUpdate: AsksUpdate? = null,
    val bidsUpdate: BidsUpdate? = null,
    val channelName: String,
    val pair: String,
) : KrakenData {
    override val type: String = "update"
}

object BookUpdateSerializer :
    JsonTransformingSerializer<BookUpdate>(BookUpdate.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {

        return element.jsonArray.let {
            val firstUpdateAction = it[1]
            val secondUpdateAction = if (it.size > 4) {
                it[2]
            } else {
                null
            }

            val asksUpdate = if ("a" in firstUpdateAction.jsonObject.keys) {
                firstUpdateAction
            } else {
                null
            }

            val bidsUpdate = if (asksUpdate == null) {
                firstUpdateAction
            } else {
                secondUpdateAction
            }

            buildJsonObject {
                put("channelId", it[0].toString().toInt())
                if (asksUpdate != null) {
                    put("asksUpdate", asksUpdate)
                }
                if (bidsUpdate != null) {
                    put("bidsUpdate", bidsUpdate)
                }
                put("channelName", it[it.size - 2])
                put("pair", it.last())
            }
        }
    }
}


object KrakenDataSerializer : JsonContentPolymorphicSerializer<KrakenData>(KrakenData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element is JsonObject -> Event.serializer()
        else -> with(element.jsonArray[element.jsonArray.size - 2].toString()) {
            when {
                this == "\"trade\"" -> TradeSerializer
                this.startsWith("\"book") -> with(element.jsonArray[1].jsonObject) {
                    when {
                        "as" in this.keys -> BookSnapshotSerializer
                        else -> BookUpdateSerializer
                    }
                }
                else -> Event.serializer()
            }
        }
    }
}

