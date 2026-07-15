from django.urls import path

from .views import CurrentUserView, LoginView, RegisterView

urlpatterns = [
    path("auth/register", RegisterView.as_view(), name="register"),
    path("auth/login", LoginView.as_view(), name="login"),
    path("users/me", CurrentUserView.as_view(), name="current-user"),
]
