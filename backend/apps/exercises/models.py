import uuid

from django.db import models


class MuscleGroup(models.TextChoices):
    CHEST = "CHEST"
    BACK = "BACK"
    LEGS = "LEGS"
    SHOULDERS = "SHOULDERS"
    ARMS = "ARMS"
    CORE = "CORE"
    FULL_BODY = "FULL_BODY"


class Equipment(models.TextChoices):
    NONE = "NONE"
    DUMBBELL = "DUMBBELL"
    BARBELL = "BARBELL"
    MACHINE = "MACHINE"
    RESISTANCE_BAND = "RESISTANCE_BAND"
    KETTLEBELL = "KETTLEBELL"


class RiskFactor(models.TextChoices):
    SPINAL_AXIAL_LOAD = "SPINAL_AXIAL_LOAD"
    LUMBAR_FLEXION = "LUMBAR_FLEXION"
    HIGH_IMPACT = "HIGH_IMPACT"
    HEAVY_GRIP = "HEAVY_GRIP"
    OVERHEAD_LOAD = "OVERHEAD_LOAD"
    JOINT_INTENSIVE = "JOINT_INTENSIVE"


class Exercise(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    name = models.CharField(max_length=120)
    muscle_group = models.CharField(max_length=20, choices=MuscleGroup.choices)
    equipment = models.CharField(max_length=20, choices=Equipment.choices)
    media_url = models.URLField()
    instructions = models.TextField()
    risk_factors = models.JSONField(default=list, blank=True)
    safe_alternative = models.ForeignKey(
        "self", null=True, blank=True, on_delete=models.SET_NULL, related_name="+"
    )

    class Meta:
        ordering = ["name"]

    def __str__(self) -> str:
        return self.name
