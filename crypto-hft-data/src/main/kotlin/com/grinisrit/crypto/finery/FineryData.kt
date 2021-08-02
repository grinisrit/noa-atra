package com.grinisrit.crypto.finery

import com.grinisrit.crypto.common.models.PlatformData
import com.grinisrit.crypto.common.models.UnbookedEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

interface FineryData : PlatformData

@Serializable
data class BookLevel(
    val price: Long,
    val size: Long,
)

object BookLevelSerializer :
    JsonTransformingSerializer<BookLevel>(BookLevel.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("price", it[0])
                put("size", it[1])
            }
        }
    }
}

@Serializable
data class BookUpdate(
    val action: Char,
    val price: Long,
    val size: Long,
)

object BookUpdateSerializer :
    JsonTransformingSerializer<BookUpdate>(BookUpdate.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("action", it[0])
                put("price", it[1])
                put("size", it[2])
            }
        }
    }
}

@Serializable
data class SnapshotData(
    val bids: List<@Serializable(with = BookLevelSerializer::class) BookLevel>,
    val asks: List<@Serializable(with = BookLevelSerializer::class) BookLevel>,
)

object SnapshotDataSerializer :
    JsonTransformingSerializer<SnapshotData>(SnapshotData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("bids", it[0])
                put("asks", it[1])
            }
        }
    }
}

@Serializable
data class Snapshot(
    val feed: Char,
    val feedId: Long,
    val dataType: Char,
    @Serializable(with = SnapshotDataSerializer::class) val data: SnapshotData,
) : FineryData {
    val symbol = feedIdToSymbol[feedId]
    override val type = FineryDataType.snapshot
}

object SnapshotSerializer :
    JsonTransformingSerializer<Snapshot>(Snapshot.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("feed", it[0])
                put("feedId", it[1])
                put("dataType", it[2])
                put("data", it[3])
            }
        }
    }
}


@Serializable
data class UpdatesData(
    val bids: List<@Serializable(with = BookUpdateSerializer::class) BookUpdate>,
    val asks: List<@Serializable(with = BookUpdateSerializer::class) BookUpdate>,
)

object UpdatesDataSerializer :
    JsonTransformingSerializer<UpdatesData>(UpdatesData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("bids", it[0])
                put("asks", it[1])
            }
        }
    }
}

@Serializable
data class Updates(
    val feed: Char,
    val feedId: Long,
    val dataType: Char,
    @Serializable(with = UpdatesDataSerializer::class) val data: UpdatesData,
) : FineryData {
    val symbol = feedIdToSymbol[feedId]
    override val type = FineryDataType.updates
}

object UpdatesSerializer :
    JsonTransformingSerializer<Updates>(Updates.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return element.jsonArray.let {
            buildJsonObject {
                put("feed", it[0])
                put("feedId", it[1])
                put("dataType", it[2])
                put("data", it[3])
            }
        }
    }
}

@Serializable
class Event : FineryData, UnbookedEvent

object EventSerializer :
    JsonTransformingSerializer<Event>(Event.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        return buildJsonObject {}
    }
}

object FineryDataSerializer : JsonContentPolymorphicSerializer<FineryData>(FineryData::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        element !is JsonArray -> EventSerializer
        else -> element.jsonArray.let { mainArray ->
            when {
                mainArray.size < 3 -> EventSerializer
                mainArray.first() !is JsonPrimitive -> EventSerializer
                mainArray.first().jsonPrimitive.content != "B" -> EventSerializer
                mainArray[2] !is JsonPrimitive -> Event.serializer()
                else -> with(mainArray[2].jsonPrimitive.content) {
                    when (this) {
                        "S" -> SnapshotSerializer
                        "M" -> UpdatesSerializer
                        else -> EventSerializer
                    }
                }
            }
        }
    }
}
