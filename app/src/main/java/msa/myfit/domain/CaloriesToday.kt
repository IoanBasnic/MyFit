package msa.myfit.domain

import java.time.DayOfWeek
import java.time.LocalDate

data class CaloriesToday(
    val calorieSum: Float,
    val dayOfWeek: DayOfWeek,
    val dateTime: LocalDate
)

data class DistanceToday(
    val distanceSum: Float,
    val dayOfWeek: DayOfWeek,
    val dateTime: LocalDate
)

data class WeightToday(
    val weight: Float,
    val dayOfWeek: DayOfWeek,
    val dateTime: LocalDate
)