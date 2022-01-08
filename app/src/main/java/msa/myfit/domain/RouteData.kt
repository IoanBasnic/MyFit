package msa.myfit.domain

import java.time.Duration
import java.time.LocalDateTime

data class RouteData(
    val routeTime: Duration,
    val distanceInKm: Float,
    val caloriesBurnt: Float,
    val pointsEarned: Long,
    val startDateTime: LocalDateTime
)
