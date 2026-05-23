package com.wassima.leanmass.utils

object LBMCalculator {
    fun calculate(
        weight: Double,
        height: Double,
        gender: String
    ): Double = when (gender) {
        "male"   -> (0.407 * weight) + (0.267 * height) - 19.2
        "female" -> (0.252 * weight) + (0.473 * height) - 48.3
        else     -> throw IllegalArgumentException("Genre inconnu")
    }

    fun isSatisfactory(
        lbm: Double,
        gender: String
    ): Boolean = when (gender) {
        "male"   -> lbm >= Constants.LBM_MIN_MALE
        "female" -> lbm >= Constants.LBM_MIN_FEMALE
        else     -> false
    }
}