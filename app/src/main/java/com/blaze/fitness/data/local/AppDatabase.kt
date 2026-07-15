package com.blaze.fitness.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blaze.fitness.data.local.dao.AchievementDao
import com.blaze.fitness.data.local.dao.ExerciseDao
import com.blaze.fitness.data.local.dao.QuestionnaireDao
import com.blaze.fitness.data.local.dao.UserDao
import com.blaze.fitness.data.local.dao.WorkoutDao
import com.blaze.fitness.data.local.entity.AchievementEntity
import com.blaze.fitness.data.local.entity.ExerciseEntity
import com.blaze.fitness.data.local.entity.QuestionnaireEntity
import com.blaze.fitness.data.local.entity.UserEntity
import com.blaze.fitness.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [
        UserEntity::class,
        QuestionnaireEntity::class,
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        AchievementEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "blaze.db"
    }
}
