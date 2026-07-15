from collections import defaultdict

from rest_framework import serializers

from apps.exercises.serializers import ExerciseSerializer

from .models import WorkoutSession, WorkoutSetLog


class WorkoutSetSerializer(serializers.Serializer):
    setNumber = serializers.IntegerField(source="set_number")
    reps = serializers.IntegerField()
    weightKg = serializers.FloatField(source="weight_kg")
    completed = serializers.BooleanField()


class WorkoutExerciseInputSerializer(serializers.Serializer):
    exerciseId = serializers.UUIDField()
    sets = WorkoutSetSerializer(many=True)


class WorkoutSessionWriteSerializer(serializers.Serializer):
    id = serializers.UUIDField()
    planId = serializers.CharField()
    startedAt = serializers.IntegerField()
    completedAt = serializers.IntegerField(allow_null=True, required=False)
    loggedExercises = serializers.ListField(child=serializers.DictField())


class WorkoutExerciseOutputSerializer(serializers.Serializer):
    exercise = ExerciseSerializer()
    sets = WorkoutSetSerializer(many=True)


class WorkoutSessionSerializer(serializers.ModelSerializer):
    planId = serializers.CharField(source="plan_id")
    startedAt = serializers.SerializerMethodField()
    completedAt = serializers.SerializerMethodField()
    loggedExercises = serializers.SerializerMethodField()

    class Meta:
        model = WorkoutSession
        fields = ["id", "planId", "startedAt", "completedAt", "loggedExercises"]

    def get_startedAt(self, obj: WorkoutSession) -> int:
        return int(obj.started_at.timestamp() * 1000)

    def get_completedAt(self, obj: WorkoutSession):
        return int(obj.completed_at.timestamp() * 1000) if obj.completed_at else None

    def get_loggedExercises(self, obj: WorkoutSession) -> list:
        by_exercise = defaultdict(list)
        for log in obj.set_logs.select_related("exercise"):
            by_exercise[log.exercise].append(log)

        return [
            {
                "exercise": ExerciseSerializer(exercise).data,
                "sets": WorkoutSetSerializer(logs, many=True).data,
            }
            for exercise, logs in by_exercise.items()
        ]


def create_session_with_logs(*, user, validated_data: dict) -> WorkoutSession:
    from datetime import datetime, timezone

    from apps.exercises.models import Exercise

    started_at = datetime.fromtimestamp(validated_data["startedAt"] / 1000, tz=timezone.utc)
    completed_ms = validated_data.get("completedAt")
    completed_at = datetime.fromtimestamp(completed_ms / 1000, tz=timezone.utc) if completed_ms else None

    session = WorkoutSession.objects.create(
        id=validated_data["id"],
        user=user,
        plan_id=validated_data["planId"],
        started_at=started_at,
        completed_at=completed_at,
    )

    set_logs = []
    for entry in validated_data["loggedExercises"]:
        exercise = Exercise.objects.get(id=entry["exercise"]["id"])
        for workout_set in entry["sets"]:
            set_logs.append(
                WorkoutSetLog(
                    session=session,
                    exercise=exercise,
                    set_number=workout_set["setNumber"],
                    reps=workout_set["reps"],
                    weight_kg=workout_set["weightKg"],
                    completed=workout_set["completed"],
                )
            )
    WorkoutSetLog.objects.bulk_create(set_logs)
    return session
