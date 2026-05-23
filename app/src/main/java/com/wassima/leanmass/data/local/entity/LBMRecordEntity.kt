package com.wassima.leanmass.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lbm_records")
data class LBMRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val weight: Double,
    val height: Double,
    val gender: String,
    val lbmResult: Double,
    val isSatisfactory: Boolean,
    val timestamp: Long
)