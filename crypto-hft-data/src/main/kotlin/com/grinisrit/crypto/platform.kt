package com.grinisrit.crypto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlinx.serialization.Serializable

interface Platform

@Serializable
data class Coinbase(
    val address: String,
    val status: String,
) : Platform