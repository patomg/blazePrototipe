package com.blaze.fitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blaze.fitness.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
) {
    fun toDomain() = User(id = id, name = name, email = email)

    companion object {
        fun fromDomain(user: User) = UserEntity(id = user.id, name = user.name, email = user.email)
    }
}
