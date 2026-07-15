package com.blaze.fitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blaze.fitness.domain.model.WorkoutExercise
import com.blaze.fitness.domain.model.WorkoutSession
import java.time.Instant

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val planId: String,
    val startedAt: Instant,
    val completedAt: Instant?,
    val loggedExercises: List<WorkoutExercise>,
) {
    fun toDomain() = WorkoutSession(
        id = id,
        planId = planId,
        startedAt = startedAt,
        completedAt = completedAt,
        loggedExercises = loggedExercises,
    )

    companion object {
        fun fromDomain(session: WorkoutSession) = WorkoutSessionEntity(
            id = session.id,
            planId = session.planId,
            startedAt = session.startedAt,
            completedAt = session.completedAt,
            loggedExercises = session.loggedExercises,
        )
    }
}
