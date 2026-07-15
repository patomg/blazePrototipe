from rest_framework import serializers

from .models import Questionnaire


class QuestionnaireSerializer(serializers.ModelSerializer):
    userId = serializers.PrimaryKeyRelatedField(source="user", read_only=True)
    weightKg = serializers.FloatField(source="weight_kg")
    experienceLevel = serializers.CharField(source="experience_level")
    hasLumbarPain = serializers.BooleanField(source="has_lumbar_pain")
    hasHernia = serializers.BooleanField(source="has_hernia")

    class Meta:
        model = Questionnaire
        fields = [
            "userId",
            "weightKg",
            "age",
            "sex",
            "experienceLevel",
            "hasLumbarPain",
            "hasHernia",
            "disabilities",
            "goal",
        ]

    def validate_age(self, value: int) -> int:
        if value <= 0 or value > 120:
            raise serializers.ValidationError("Edad fuera de rango válido")
        return value

    def validate_weightKg(self, value: float) -> float:
        if value <= 0 or value > 400:
            raise serializers.ValidationError("Peso fuera de rango válido")
        return value
