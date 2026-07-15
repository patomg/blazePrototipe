package com.blaze.fitness.data.remote.dto

import com.blaze.fitness.domain.model.Equipment
import com.blaze.fitness.domain.model.Exercise
import com.blaze.fitness.domain.model.ExperienceLevel
import com.blaze.fitness.domain.model.Goal
import com.blaze.fitness.domain.model.MuscleGroup
import com.blaze.fitness.domain.model.Questionnaire
import com.blaze.fitness.domain.model.RiskFactor
import com.blaze.fitness.domain.model.Sex
import com.blaze.fitness.domain.model.User
import com.blaze.fitness.domain.model.WorkoutExercise
import com.blaze.fitness.domain.model.WorkoutSession
import com.blaze.fitness.domain.model.WorkoutSet
import java.time.Instant

fun UserDto.toDomain() = User(id = id, name = name, email = email)

fun Exercise.toDto() = ExerciseDto(
    id = id,
    name = name,
    muscleGroup = muscleGroup.name,
    equipment = equipment.name,
    mediaUrl = mediaUrl,
    instructions = instructions,
    riskFactors = riskFactors.map { it.name }.toSet(),
    safeAlternativeId = safeAlternativeId,
)

fun ExerciseDto.toDomain() = Exercise(
    id = id,
    name = name,
    muscleGroup = MuscleGroup.valueOf(muscleGroup),
    equipment = Equipment.valueOf(equipment),
    mediaUrl = mediaUrl,
    instructions = instructions,
    riskFactors = riskFactors.map(RiskFactor::valueOf).toSet(),
    safeAlternativeId = safeAlternativeId,
)

fun Questionnaire.toDto() = QuestionnaireDto(
    userId = userId,
    weightKg = weightKg,
    age = age,
    sex = sex.name,
    experienceLevel = experienceLevel.name,
    hasLumbarPain = hasLumbarPain,
    hasHernia = hasHernia,
    disabilities = disabilities,
    goal = goal.name,
)

fun QuestionnaireDto.toDomain() = Questionnaire(
    userId = userId,
    weightKg = weightKg,
    age = age,
    sex = Sex.valueOf(sex),
    experienceLevel = ExperienceLevel.valueOf(experienceLevel),
    hasLumbarPain = hasLumbarPain,
    hasHernia = hasHernia,
    disabilities = disabilities,
    goal = Goal.valueOf(goal),
)

fun WorkoutSet.toDto() = WorkoutSetDto(setNumber = setNumber, reps = reps, weightKg = weightKg, completed = completed)

fun WorkoutSetDto.toDomain() = WorkoutSet(setNumber = setNumber, reps = reps, weightKg = weightKg, completed = completed)

fun WorkoutExercise.toDto() = WorkoutExerciseDto(exercise = exercise.toDto(), sets = sets.map { it.toDto() })

fun WorkoutExerciseDto.toDomain() = WorkoutExercise(exercise = exercise.toDomain(), sets = sets.map { it.toDomain() })

fun WorkoutSession.toDto() = WorkoutSessionDto(
    id = id,
    planId = planId,
    startedAt = startedAt.toEpochMilli(),
    completedAt = completedAt?.toEpochMilli(),
    loggedExercises = loggedExercises.map { it.toDto() },
)

fun WorkoutSessionDto.toDomain() = WorkoutSession(
    id = id,
    planId = planId,
    startedAt = Instant.ofEpochMilli(startedAt),
    completedAt = completedAt?.let(Instant::ofEpochMilli),
    loggedExercises = loggedExercises.map { it.toDomain() },
)
