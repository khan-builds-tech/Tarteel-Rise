package com.example.core.utils

import kotlin.math.max
import kotlin.math.min

object TextMatcher {

    /**
     * Normalizes Arabic text by removing diacritics, normalizing Alifs,
     * removing punctuation and extra whitespace.
     */
    fun normalize(text: String): String {
        var result = text

        // Strip tashkeel (vowel marks)
        // \u064B (Fathatayn), \u064C (Dammatayn), \u064D (Kasratayn),
        // \u064E (Fatha), \u064F (Damma), \u0650 (Kasra),
        // \u0651 (Shadda), \u0652 (Sukun), \u0670 (Super Alif), \u0640 (Tatweel)
        val tashkeelRegex = Regex("[\u064B-\u0652\u0670\u0640]")
        result = result.replace(tashkeelRegex, "")

        // Normalize Alif forms (أ , إ , آ , ٱ) to bare Alif (ا)
        result = result.replace(Regex("[أإآٱ]"), "ا")

        // Normalize Teh Marbuta (ة) to Heh (ه)
        result = result.replace("ة", "ه")

        // Normalize Alef Maksura (ى) to Yeh (ي)
        result = result.replace("ى", "ي")

        // Remove standard and Arabic punctuation/numbers/symbols
        // Keeps letters and spaces
        val nonLetterRegex = Regex("[^\\s\\p{L}]")
        result = result.replace(nonLetterRegex, " ")

        // Trim extraneous whitespaces
        result = result.replace(Regex("\\s+"), " ").trim()

        return result
    }

    /**
     * Calculates the similarity ratio between two strings using Levenshtein distance.
     * Returns a value between 0.0 and 1.0.
     */
    fun calculateSimilarity(str1: String, str2: String): Double {
        val s1 = normalize(str1).lowercase()
        val s2 = normalize(str2).lowercase()

        if (s1 == s2) return 1.0
        if (s1.isEmpty() || s2.isEmpty()) return 0.0

        val distance = levenshteinDistance(s1, s2)
        val maxLength = max(s1.length, s2.length)
        
        // Match percentage
        return 1.0 - (distance.toDouble() / maxLength)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = IntArray(s2.length + 1) { it }
        for (i in 1..s1.length) {
            var prev = dp[0]
            dp[0] = i
            for (j in 1..s2.length) {
                val temp = dp[j]
                if (s1[i - 1] == s2[j - 1]) {
                    dp[j] = prev
                } else {
                    dp[j] = min(min(dp[j] + 1, dp[j - 1] + 1), prev + 1)
                }
                prev = temp
            }
        }
        return dp[s2.length]
    }
}
