package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AyahRecord(
    val id: Int,
    val text: String,
    val translation: String
)

@JsonClass(generateAdapter = true)
data class Surah(
    val id: Int,
    val name: String,
    val transliteration: String,
    val translation: String,
    val type: String,
    @Json(name = "total_verses") val totalVerses: Int,
    val verses: List<AyahRecord>
)
