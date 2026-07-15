# Blaze Backend

Django REST Framework API que respalda la app Blaze: autenticación JWT, cuestionario
médico, catálogo de ejercicios y registro de entrenamientos. Todas las rutas están
versionadas bajo `/api/v1/`.

## Requisitos

- Python 3.12
- PostgreSQL 16 (o Docker)

## Setup local

```bash
python -m venv .venv && source .venv/bin/activate
pip install -r requirements-dev.txt
cp .env.example .env   # edita DJANGO_SECRET_KEY y POSTGRES_PASSWORD
python manage.py migrate
python manage.py seed_exercises   # catálogo de ejemplo
python manage.py createsuperuser
python manage.py runserver
```

## Con Docker

Desde la raíz del repositorio:

```bash
cp backend/.env.example backend/.env
docker compose up --build
```

## Tests

```bash
pytest --cov=apps
flake8 .
```

## Endpoints principales

| Método | Ruta                          | Descripción                                   |
| ------ | ----------------------------- | ---------------------------------------------- |
| POST   | `/api/v1/auth/register`       | Crea usuario, devuelve tokens JWT              |
| POST   | `/api/v1/auth/login`          | Autentica, devuelve tokens JWT                 |
| GET    | `/api/v1/users/me`            | Usuario autenticado actual                     |
| POST   | `/api/v1/questionnaire`       | Crea/actualiza el cuestionario del usuario     |
| GET    | `/api/v1/questionnaire/{id}`  | Cuestionario del usuario (solo el propio)      |
| GET    | `/api/v1/exercises`           | Catálogo de ejercicios con `riskFactors`       |
| GET    | `/api/v1/workouts/sessions`   | Historial de entrenamientos del usuario        |
| POST   | `/api/v1/workouts/sessions`   | Registra una sesión de entrenamiento completada|

## Seguridad

- Contraseñas hasheadas por Django (`PBKDF2`), nunca en texto plano.
- Autenticación por JWT (`djangorestframework-simplejwt`).
- `DJANGO_SECRET_KEY` y `POSTGRES_PASSWORD` son obligatorios vía variables de entorno; la
  app no arranca con valores por defecto hardcodeados.
- Los endpoints de cuestionario están restringidos a su propio dueño (ver
  `apps/questionnaire/views.py`).
