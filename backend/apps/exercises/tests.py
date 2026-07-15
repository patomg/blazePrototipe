import pytest
from rest_framework.test import APIClient

from apps.users.models import User

from .models import Equipment, Exercise, MuscleGroup, RiskFactor


@pytest.mark.django_db
def test_exercise_list_requires_authentication():
    client = APIClient()
    response = client.get("/api/v1/exercises")
    assert response.status_code == 401


@pytest.mark.django_db
def test_exercise_list_returns_risk_factors():
    user = User.objects.create_user(email="ada@example.com", password="supersecret1", name="Ada")
    Exercise.objects.create(
        name="Peso muerto",
        muscle_group=MuscleGroup.BACK,
        equipment=Equipment.BARBELL,
        media_url="https://example.com/deadlift.mp4",
        instructions="Mantén la espalda neutra durante todo el movimiento.",
        risk_factors=[RiskFactor.SPINAL_AXIAL_LOAD, RiskFactor.LUMBAR_FLEXION],
    )

    client = APIClient()
    client.force_authenticate(user=user)
    response = client.get("/api/v1/exercises")

    assert response.status_code == 200
    assert response.data[0]["riskFactors"] == ["SPINAL_AXIAL_LOAD", "LUMBAR_FLEXION"]
