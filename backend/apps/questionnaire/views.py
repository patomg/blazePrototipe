from rest_framework import permissions, status
from rest_framework.generics import RetrieveAPIView
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import Questionnaire
from .serializers import QuestionnaireSerializer


class SubmitQuestionnaireView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        instance = Questionnaire.objects.filter(user=request.user).first()
        serializer = QuestionnaireSerializer(instance, data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save(user=request.user)
        response_status = status.HTTP_201_CREATED if instance is None else status.HTTP_200_OK
        return Response(serializer.data, status=response_status)


class QuestionnaireDetailView(RetrieveAPIView):
    serializer_class = QuestionnaireSerializer
    permission_classes = [permissions.IsAuthenticated]
    lookup_field = "user_id"
    lookup_url_kwarg = "user_id"

    def get_queryset(self):
        # Users may only ever read their own questionnaire, regardless of the id in the URL.
        return Questionnaire.objects.filter(user=self.request.user)
