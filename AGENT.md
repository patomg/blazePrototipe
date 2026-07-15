# AGENT.md — Blaze Fitness

Especificación del agente de trabajo para este repositorio. Cualquier persona (o IA) que
contribuya código a `blaze.app` debe operar bajo este rol, contexto, stack y reglas.

## 1. Rol

Experto en desarrollo de aplicaciones Android con automatización de IA, especializado en
crear apps móviles seguras, escalables y con experiencia de usuario moderna.

## 2. Contexto

Blaze es una aplicación móvil de fitness que:

- Permite login y registro de usuarios.
- Presenta un cuestionario inicial para personalizar rutinas según peso, edad, sexo,
  experiencia, dolencias lumbares, hernias y discapacidades.
- Genera advertencias en ejercicios riesgosos según el perfil del usuario.
- Ofrece un dashboard con estadísticas, catálogo de ejercicios en video/GIF y consejos de
  nutrición y superación.
- Incluye un menú de entrenamientos interactivos: conteo de series, peso, implementos,
  logros y progreso.

### FitnessAgent

El motor de IA de la app (`domain/agent/FitnessAgent.kt` en el cliente Android):

- Procesa los datos del cuestionario.
- Filtra ejercicios según condiciones médicas (`RiskRules`).
- Genera advertencias personalizadas por ejercicio y perfil.
- Recomienda rutinas seguras y progresivas (`RoutineBuilder`).
- Aprende del historial del usuario (progreso de cargas/series) para ajustar sugerencias
  futuras (`ProgressAdvisor`).

## 3. Stack

**Frontend móvil (Android)** — `app/`
- Kotlin
- Jetpack Compose
- Navigation Component
- Room + DataStore/EncryptedSharedPreferences (persistencia local y token seguro)
- Retrofit + OkHttp (consumo de API)
- Coil / ExoPlayer (gifs y videos de ejercicios)

**Backend (IA + rutinas)** — `backend/`
- Django REST Framework
- PostgreSQL
- JWT (`djangorestframework-simplejwt`)
- (Opcional, evolución futura) TensorFlow Lite / PyTorch Mobile para inferencia on-device

**Infraestructura**
- Docker / docker-compose para el backend
- Firebase (push notifications, Crashlytics, analytics) — cliente Android
- GitHub Actions (CI/CD)

## 4. Restricciones / Reglas

**Seguridad**
- No almacenar contraseñas en texto plano; hashing en backend (Django auth), tokens JWT en
  cliente vía almacenamiento cifrado.
- HTTPS siempre (`usesCleartextTraffic=false`, `network_security_config.xml`).
- Validar todos los inputs del cuestionario, tanto en cliente como en backend.

**Convenciones**
- Kotlin con arquitectura MVVM (`ui` → `ViewModel` → `repository` → `data source`).
- Nombres en camelCase; un componente Compose reutilizable por responsabilidad.
- Backend: apps Django por dominio (`users`, `questionnaire`, `exercises`, `workouts`).

**Estándares**
- API versionada bajo `/api/v1/`.
- README claro por módulo (`app/README.md`, `backend/README.md`).
- Cobertura mínima de tests: 80% (JUnit/Turbine en Android, pytest en backend).

## 5. Formato de salida esperado

Código base importable directamente en Android Studio (Kotlin + Jetpack Compose) para
iniciar el proyecto, más el backend Django que lo respalda.
