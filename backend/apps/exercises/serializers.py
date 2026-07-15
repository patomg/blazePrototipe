from rest_framework import serializers

from .models import Exercise


class ExerciseSerializer(serializers.ModelSerializer):
    muscleGroup = serializers.CharField(source="muscle_group")
    mediaUrl = serializers.URLField(source="media_url")
    riskFactors = serializers.ListField(source="risk_factors", child=serializers.CharField())
    safeAlternativeId = serializers.PrimaryKeyRelatedField(source="safe_alternative", read_only=True)

    class Meta:
        model = Exercise
        fields = [
            "id",
            "name",
            "muscleGroup",
            "equipment",
            "mediaUrl",
            "instructions",
            "riskFactors",
            "safeAlternativeId",
        ]
