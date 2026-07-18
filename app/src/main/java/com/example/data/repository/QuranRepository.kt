package com.example.data.repository

import android.content.Context
import com.example.data.model.AyahRecord
import com.example.data.model.Surah
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuranRepository {
    private var cachedSurahs: List<Surah>? = null
    private var surahMap: Map<Int, Surah>? = null

    suspend fun getSurahs(context: Context): List<Surah> = withContext(Dispatchers.IO) {
        cachedSurahs?.let { return@withContext it }

        try {
            val jsonString = context.assets.open("data/quran_en.json").bufferedReader().use { it.readText() }
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val listType = Types.newParameterizedType(List::class.java, Surah::class.java)
            val adapter = moshi.adapter<List<Surah>>(listType)
            val surahs = adapter.fromJson(jsonString) ?: emptyList()
            
            cachedSurahs = surahs
            surahMap = surahs.associateBy { it.id }
            surahs
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getSurahById(context: Context, id: Int): Surah? {
        if (surahMap == null) {
            getSurahs(context)
        }
        return surahMap?.get(id)
    }

    suspend fun getAyah(context: Context, surahId: Int, verseId: Int): AyahRecord? {
        val surah = getSurahById(context, surahId)
        return surah?.verses?.firstOrNull { it.id == verseId }
    }
}
