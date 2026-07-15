import uuid

from django.conf import settings
from django.db import models

from apps.exercises.models import Exercise


class WorkoutSession(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(
        settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="workout_sessions"
    )
    plan_id = models.CharField(max_length=64)
    started_at = models.DateTimeField()
    completed_at = models.DateTimeField(null=True, blank=True)

    class Meta:
        ordering = ["-started_at"]

    def __str__(self) -> str:
        return f"{self.user.email} - {self.plan_id}"


class WorkoutSetLog(models.Model):
    session = models.ForeignKey(WorkoutSession, on_delete=models.CASCADE, related_name="set_logs")
    exercise = models.ForeignKey(Exercise, on_delete=models.PROTECT, related_name="+")
    set_number = models.PositiveSmallIntegerField()
    reps = models.PositiveSmallIntegerField()
    weight_kg = models.FloatField()
    completed = models.BooleanField(default=False)

    class Meta:
        ordering = ["exercise_id", "set_number"]


class Achievement(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="achievements")
    title = models.CharField(max_length=120)
    description = models.CharField(max_length=255)
    unlocked_at = models.DateTimeField(null=True, blank=True)

    def __str__(self) -> str:
        return self.title
