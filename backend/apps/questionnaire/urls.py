from django.urls import path

from .views import QuestionnaireDetailView, SubmitQuestionnaireView

urlpatterns = [
    path("questionnaire", SubmitQuestionnaireView.as_view(), name="questionnaire-submit"),
    path("questionnaire/<int:user_id>", QuestionnaireDetailView.as_view(), name="questionnaire-detail"),
]
