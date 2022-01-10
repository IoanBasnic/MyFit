package msa.myfit.domain

import java.time.Duration

object DatabaseVariables {
    //user profile
    //database name
    val userProfileDatabase = "user_profiles"
    //variables
    val correlationId = "correlation_id"
    val firstName = "first_name"
    val lastName = "last_name"
    val gender = "gender"
    val age = "age"
    val height = "height"

    //calories eaten
    //database name
    val foodDatabase = "calories_intake"
    //variables
    val userId = "user_id"
    val calories = "calories"
    val name = "name"
    val foodType = "type"
    val inputDate = "input_date"

    //route result
    //database name
    val routeDatabase = "route"
    //variables
    //val userId = "user_id"
    val routeTime = "route_time"
    val distanceInKm = "distance_km"
    val caloriesBurnt = "calories_burnt"
    val pointsEarned = "points_earned"
    val startDate = "start_date"

    //weight for today
    //database name
    val weightConsumedDatabase = "weights"
    //variables
    //val userId = "user_id"
    val weight = "weight"
    //val inputDate = "input_date"

    //weight goal
    val weightGoalDatabase = "weight_goal"
    //variables
    //val userId = "user_id"
    //val weight = "weight"
}