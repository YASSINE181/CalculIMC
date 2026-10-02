package com.example.calculimc

import android.os.Parcelable
import androidx.annotation.ColorRes

/**
 * Model class representing a completed IMC calculation record.
 */
data class ImcRecord(
    val poidsKg: Double,
    val tailleM: Double,
    val imcValue: Double,
    val categoryText: String,
    @ColorRes val categoryColorRes: Int,
    val timestamp: Long = System.currentTimeMillis()
)
