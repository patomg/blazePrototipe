# Blaze

Aplicación móvil de fitness con un motor de IA (`FitnessAgent`) que personaliza rutinas y
advierte de ejercicios riesgosos según el perfil médico del usuario. Ver [AGENT.md](AGENT.md)
para el rol, contexto y reglas completas del proyecto.

## Estructura del repositorio

```
.
├── app/        # Cliente Android (Kotlin + Jetpack Compose, MVVM)
├── backend/    # API Django REST Framework + PostgreSQL
├── AGENT.md    # Rol, contexto, stack y reglas del proyecto
└── docker-compose.yml
```

## App Android

Abre la raíz del repositorio en Android Studio (Hedgehog o superior). Si `./gradlew` no
tiene el JAR del wrapper (se excluye de este entorno por restricciones de red), Android
Studio lo generará automáticamente al sincronizar, o puedes ejecutar `gradle wrapper`
localmente antes de abrir el proyecto.

- Mínimo SDK 26, compilado con SDK 34.
- Arquitectura: `ui` (Compose) → `ViewModel` → `repository` → `Room`/`Retrofit`.
- El motor de recomendaciones vive en `app/src/main/java/com/blaze/fitness/domain/agent/`.
- Tests unitarios: `./gradlew testDebugUnitTest`.

Configura la URL del backend en `app/build.gradle.kts` (`API_BASE_URL` por build type).

## Backend

Ver [backend/README.md](backend/README.md) para instrucciones detalladas.

```bash
cp backend/.env.example backend/.env   # y edita los secretos
docker compose up --build
```

La API queda disponible en `http://localhost:8000/api/v1/`.

## CI

- `.github/workflows/android-ci.yml`: tests unitarios y lint del cliente Android.
- `.github/workflows/backend-ci.yml`: lint, chequeo de migraciones y tests del backend.
