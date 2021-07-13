package com.grinisrit.crypto.common

interface CustomJsonParser<T: ChannelData> {
    fun parse(jsonString: String): T
}