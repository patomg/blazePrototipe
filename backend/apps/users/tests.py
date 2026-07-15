import pytest
from rest_framework.test import APIClient

from .models import User


@pytest.mark.django_db
def test_register_creates_user_and_returns_tokens():
    client = APIClient()
    response = client.post(
        "/api/v1/auth/register",
        {"name": "Ada", "email": "ada@example.com", "password": "supersecret1"},
        format="json",
    )

    assert response.status_code == 201
    assert "accessToken" in response.data
    assert "refreshToken" in response.data
    assert response.data["user"]["email"] == "ada@example.com"
    assert User.objects.filter(email="ada@example.com").exists()


@pytest.mark.django_db
def test_register_does_not_store_plaintext_password():
    client = APIClient()
    client.post(
        "/api/v1/auth/register",
        {"name": "Ada", "email": "ada@example.com", "password": "supersecret1"},
        format="json",
    )

    user = User.objects.get(email="ada@example.com")
    assert user.password != "supersecret1"
    assert user.check_password("supersecret1")


@pytest.mark.django_db
def test_login_with_wrong_password_is_rejected():
    User.objects.create_user(email="ada@example.com", password="supersecret1", name="Ada")
    client = APIClient()

    response = client.post(
        "/api/v1/auth/login",
        {"email": "ada@example.com", "password": "wrong-password"},
        format="json",
    )

    assert response.status_code == 401


@pytest.mark.django_db
def test_current_user_requires_authentication():
    client = APIClient()
    response = client.get("/api/v1/users/me")
    assert response.status_code == 401
