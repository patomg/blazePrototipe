from rest_framework import permissions, status
from rest_framework.response import Response
from rest_framework.views import APIView

from .models import WorkoutSession
from .serializers import WorkoutSessionSerializer, WorkoutSessionWriteSerializer, create_session_with_logs


class WorkoutSessionListCreateView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        sessions = WorkoutSession.objects.filter(user=request.user).prefetch_related("set_logs__exercise")
        return Response(WorkoutSessionSerializer(sessions, many=True).data)

    def post(self, request):
        serializer = WorkoutSessionWriteSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        session = create_session_with_logs(user=request.user, validated_data=serializer.validated_data)
        return Response(WorkoutSessionSerializer(session).data, status=status.HTTP_201_CREATED)
