from django.core.management.base import BaseCommand

from apps.exercises.models import Equipment, Exercise, MuscleGroup, RiskFactor

SAMPLE_EXERCISES = [
    {
        "name": "Peso muerto",
        "muscle_group": MuscleGroup.BACK,
        "equipment": Equipment.BARBELL,
        "media_url": "https://cdn.blaze.app/exercises/deadlift.mp4",
        "instructions": "Mantén la espalda neutra y extiende cadera y rodillas al mismo tiempo.",
        "risk_factors": [RiskFactor.SPINAL_AXIAL_LOAD, RiskFactor.LUMBAR_FLEXION, RiskFactor.HEAVY_GRIP],
    },
    {
        "name": "Sentadilla goblet",
        "muscle_group": MuscleGroup.LEGS,
        "equipment": Equipment.DUMBBELL,
        "media_url": "https://cdn.blaze.app/exercises/goblet-squat.gif",
        "instructions": "Sostén la mancuerna a la altura del pecho y desciende controlando la rodilla.",
        "risk_factors": [RiskFactor.JOINT_INTENSIVE],
    },
    {
        "name": "Press militar",
        "muscle_group": MuscleGroup.SHOULDERS,
        "equipment": Equipment.BARBELL,
        "media_url": "https://cdn.blaze.app/exercises/overhead-press.mp4",
        "instructions": "Empuja la barra por encima de la cabeza manteniendo el core activo.",
        "risk_factors": [RiskFactor.OVERHEAD_LOAD],
    },
    {
        "name": "Plancha abdominal",
        "muscle_group": MuscleGroup.CORE,
        "equipment": Equipment.NONE,
        "media_url": "https://cdn.blaze.app/exercises/plank.gif",
        "instructions": "Mantén el cuerpo alineado desde los hombros hasta los talones.",
        "risk_factors": [],
    },
    {
        "name": "Remo con banda",
        "muscle_group": MuscleGroup.BACK,
        "equipment": Equipment.RESISTANCE_BAND,
        "media_url": "https://cdn.blaze.app/exercises/band-row.gif",
        "instructions": "Tira de la banda hacia el abdomen manteniendo los codos cerca del cuerpo.",
        "risk_factors": [],
    },
    {
        "name": "Zancadas",
        "muscle_group": MuscleGroup.LEGS,
        "equipment": Equipment.NONE,
        "media_url": "https://cdn.blaze.app/exercises/lunges.gif",
        "instructions": "Da un paso al frente y baja hasta que ambas rodillas formen 90 grados.",
        "risk_factors": [RiskFactor.JOINT_INTENSIVE, RiskFactor.HIGH_IMPACT],
    },
]


class Command(BaseCommand):
    help = "Seeds the exercise catalog with a starter set of exercises for local development."

    def handle(self, *args, **options):
        created = 0
        for data in SAMPLE_EXERCISES:
            _, was_created = Exercise.objects.get_or_create(name=data["name"], defaults=data)
            created += int(was_created)
        total = len(SAMPLE_EXERCISES)
        self.stdout.write(self.style.SUCCESS(f"Seeded {created} new exercise(s); {total} total in catalog."))
