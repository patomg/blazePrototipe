from django.conf import settings
from django.db import models


class Sex(models.TextChoices):
    MALE = "MALE"
    FEMALE = "FEMALE"
    OTHER = "OTHER"


class ExperienceLevel(models.TextChoices):
    BEGINNER = "BEGINNER"
    INTERMEDIATE = "INTERMEDIATE"
    ADVANCED = "ADVANCED"


class Goal(models.TextChoices):
    LOSE_WEIGHT = "LOSE_WEIGHT"
    GAIN_MUSCLE = "GAIN_MUSCLE"
    GENERAL_FITNESS = "GENERAL_FITNESS"
    MOBILITY = "MOBILITY"


class Questionnaire(models.Model):
    user = models.OneToOneField(
        settings.AUTH_USER_MODEL, on_delete=models.CASCADE, related_name="questionnaire"
    )
    weight_kg = models.FloatField()
    age = models.PositiveSmallIntegerField()
    sex = models.CharField(max_length=10, choices=Sex.choices)
    experience_level = models.CharField(max_length=15, choices=ExperienceLevel.choices)
    has_lumbar_pain = models.BooleanField(default=False)
    has_hernia = models.BooleanField(default=False)
    disabilities = models.JSONField(default=list, blank=True)
    goal = models.CharField(max_length=20, choices=Goal.choices, default=Goal.GENERAL_FITNESS)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self) -> str:
        return f"Questionnaire({self.user.email})"
