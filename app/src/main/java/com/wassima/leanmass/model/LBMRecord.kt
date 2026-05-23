package com.wassima.leanmass.model

data class LBMRecord(
    val id: String = "",
    val userId: String = "",
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val gender: String = "male",
    val lbmResult: Double = 0.0,
    val isSatisfactory: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
