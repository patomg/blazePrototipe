package com.blaze.fitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blaze.fitness.domain.model.Achievement
import java.time.Instant

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val unlockedAt: Instant?,
) {
    fun toDomain() = Achievement(id = id, title = title, description = description, unlockedAt = unlockedAt)

    companion object {
        fun fromDomain(achievement: Achievement) = AchievementEntity(
            id = achievement.id,
            title = achievement.title,
            description = achievement.description,
            unlockedAt = achievement.unlockedAt,
        )
    }
}
