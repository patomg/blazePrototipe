import pytest
from rest_framework.test import APIClient

from apps.exercises.models import Equipment, Exercise, MuscleGroup
from apps.users.models import User

from .models import WorkoutSession, WorkoutSetLog


@pytest.mark.django_db
def test_log_workout_session_creates_set_logs():
    user = User.objects.create_user(email="ada@example.com", password="supersecret1", name="Ada")
    exercise = Exercise.objects.create(
        name="Sentadilla",
        muscle_group=MuscleGroup.LEGS,
        equipment=Equipment.NONE,
        media_url="https://example.com/squat.mp4",
        instructions="Baja controlando la rodilla.",
    )
    client = APIClient()
    client.force_authenticate(user=user)

    payload = {
        "id": "5b1f3b0e-2222-4b8a-9a3d-000000000001",
        "planId": "plan-1",
        "startedAt": 1_700_000_000_000,
        "completedAt": 1_700_000_600_000,
        "loggedExercises": [
            {
                "exercise": {"id": str(exercise.id)},
                "sets": [
                    {"setNumber": 1, "reps": 12, "weightKg": 20.0, "completed": True},
                    {"setNumber": 2, "reps": 10, "weightKg": 22.5, "completed": True},
                ],
            }
        ],
    }

    response = client.post("/api/v1/workouts/sessions", payload, format="json")

    assert response.status_code == 201
    assert WorkoutSession.objects.filter(user=user).count() == 1
    assert WorkoutSetLog.objects.count() == 2
    assert response.data["loggedExercises"][0]["exercise"]["name"] == "Sentadilla"


@pytest.mark.django_db
def test_list_sessions_scoped_to_current_user():
    owner = User.objects.create_user(email="ada@example.com", password="supersecret1", name="Ada")
    other = User.objects.create_user(email="eve@example.com", password="supersecret1", name="Eve")
    WorkoutSession.objects.create(user=other, plan_id="plan-x", started_at="2024-01-01T00:00:00Z")

    client = APIClient()
    client.force_authenticate(user=owner)
    response = client.get("/api/v1/workouts/sessions")

    assert response.status_code == 200
    assert response.data == []
