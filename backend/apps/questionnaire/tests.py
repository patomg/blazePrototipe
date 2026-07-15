import pytest
from rest_framework.test import APIClient

from apps.users.models import User

from .models import Questionnaire


@pytest.fixture
def authed_client():
    user = User.objects.create_user(email="ada@example.com", password="supersecret1", name="Ada")
    client = APIClient()
    client.force_authenticate(user=user)
    return client, user


@pytest.mark.django_db
def test_submit_questionnaire_creates_record(authed_client):
    client, user = authed_client

    response = client.post(
        "/api/v1/questionnaire",
        {
            "weightKg": 70,
            "age": 30,
            "sex": "MALE",
            "experienceLevel": "BEGINNER",
            "hasLumbarPain": True,
            "hasHernia": False,
            "disabilities": [],
            "goal": "GENERAL_FITNESS",
        },
        format="json",
    )

    assert response.status_code == 201
    assert Questionnaire.objects.filter(user=user).exists()


@pytest.mark.django_db
def test_submit_questionnaire_rejects_invalid_age(authed_client):
    client, _ = authed_client

    response = client.post(
        "/api/v1/questionnaire",
        {
            "weightKg": 70,
            "age": -5,
            "sex": "MALE",
            "experienceLevel": "BEGINNER",
            "hasLumbarPain": False,
            "hasHernia": False,
            "disabilities": [],
            "goal": "GENERAL_FITNESS",
        },
        format="json",
    )

    assert response.status_code == 400


@pytest.mark.django_db
def test_user_cannot_read_another_users_questionnaire(authed_client):
    client, _ = authed_client
    other_user = User.objects.create_user(email="eve@example.com", password="supersecret1", name="Eve")
    Questionnaire.objects.create(
        user=other_user,
        weight_kg=60,
        age=25,
        sex="FEMALE",
        experience_level="BEGINNER",
    )

    response = client.get(f"/api/v1/questionnaire/{other_user.id}")

    assert response.status_code == 404
