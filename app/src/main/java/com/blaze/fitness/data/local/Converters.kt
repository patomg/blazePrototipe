package com.blaze.fitness.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.blaze.fitness.domain.model.WorkoutExercise
import java.time.Instant

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = gson.toJson(value ?: emptyList<String>())

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromRiskFactorSet(value: Set<String>?): String = gson.toJson(value ?: emptySet<String>())

    @TypeConverter
    fun toRiskFactorSet(value: String): Set<String> {
        val type = object : TypeToken<Set<String>>() {}.type
        return gson.fromJson(value, type) ?: emptySet()
    }

    @TypeConverter
    fun fromLoggedExercises(value: List<WorkoutExercise>?): String = gson.toJson(value ?: emptyList<WorkoutExercise>())

    @TypeConverter
    fun toLoggedExercises(value: String): List<WorkoutExercise> {
        val type = object : TypeToken<List<WorkoutExercise>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)
}
