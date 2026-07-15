from django.contrib import admin

from .models import Achievement, WorkoutSession, WorkoutSetLog

admin.site.register(WorkoutSession)
admin.site.register(WorkoutSetLog)
admin.site.register(Achievement)
