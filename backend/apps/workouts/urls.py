from django.urls import path

from .views import WorkoutSessionListCreateView

urlpatterns = [
    path("workouts/sessions", WorkoutSessionListCreateView.as_view(), name="workout-sessions"),
]
