package com.grinisrit.crypto.common.models

interface PlatformData {
    val type: DataType
}

interface UnbookedEvent: PlatformData {
    override val type: CommonDataType
    get() = CommonDataType.event
}
