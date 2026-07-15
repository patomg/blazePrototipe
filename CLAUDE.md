# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Blaze is a fitness mobile app: an Android client (`app/`) backed by a Django REST API
(`backend/`). Its centerpiece is `FitnessAgent`, an on-device recommendation engine that
personalizes workouts and flags risky exercises from a medical questionnaire. Full role/context/
stack/rules spec: [AGENT.md](AGENT.md). Module-specific setup: [README.md](README.md),
[backend/README.md](backend/README.md).

This repo has no `.git` (not yet initialized as a git repository).

## Commands

### Android (`app/`, Kotlin + Jetpack Compose)

Run from the repo root. On Windows, use `gradlew.bat`; if the wrapper JAR is missing (excluded
from this environment by network restrictions), point `JAVA_HOME` at Android Studio's bundled
JBR instead of trying to fetch a JDK:

```bash
JAVA_HOME="C:\Program Files\Android\Android Studio\jbr" ./gradlew.bat :app:compileDebugKotlin --console=plain
./gradlew testDebugUnitTest          # unit tests
./gradlew testDebugUnitTest --tests "com.blaze.fitness.domain.agent.FitnessAgentTest"  # single test class
./gradlew lintDebug
```

The backend URL for debug builds is set via `API_BASE_URL` in `app/build.gradle.kts` (per
build type) — update the IP there when running against a local backend from a device/emulator.

### Backend (`backend/`, Django REST Framework + PostgreSQL)

```bash
cp backend/.env.example backend/.env   # edit DJANGO_SECRET_KEY and POSTGRES_PASSWORD
python -m venv .venv && source .venv/bin/activate
pip install -r requirements-dev.txt
python manage.py migrate
python manage.py seed_exercises        # sample exercise catalog
python manage.py runserver

pytest --cov=apps                      # all tests
pytest apps/questionnaire/tests.py     # single app's tests
flake8 .
python manage.py makemigrations --check --dry-run   # CI also runs this
```

Or via Docker from the repo root: `docker compose up --build` (API at
`http://localhost:8000/api/v1/`).

### CI

- `.github/workflows/android-ci.yml`: `gradle testDebugUnitTest` + `gradle lintDebug`, triggered
  on changes under `app/`, `gradle/`, `*.gradle.kts`, `gradle.properties`.
- `.github/workflows/backend-ci.yml`: flake8, migration-drift check, `pytest --cov=apps`,
  triggered on changes under `backend/`.

## Architecture

### Android client (`app/src/main/java/com/blaze/fitness/`)

Strict MVVM layering: `ui` (Compose screens) → `ViewModel` → `repository` → `Room` (local) /
`Retrofit` (remote).

- `domain/agent/` — **FitnessAgent**, the app's AI engine. Pure logic, no I/O, kept
  unit-testable in isolation from Android framework classes:
  - `RiskRules` — filters/warns on exercises based on the user's medical profile (lumbar
    issues, hernias, disabilities, etc.) — returns `ExerciseWarning`s with severity levels;
    `AVOID`-severity warnings exclude an exercise from `filterSafeExercises`.
  - `RoutineBuilder` — builds a `WorkoutPlan` from the questionnaire + safe exercises.
  - `ProgressAdvisor` — reads `WorkoutSession` history to suggest next weight/reps.
  - `FitnessAgent` composes these three; repositories call it and persist its output — the
    agent itself has no side effects.
- `di/AppContainer.kt` — hand-rolled DI container (deliberately no Hilt/Dagger), constructed
  once with an Android `Context` and wired manually. All repositories and `FitnessAgent` are
  exposed as `val`s here; new dependencies get added to this file, not annotated into existence.
- `data/repository/` — one repository per domain (`AuthRepository`, `QuestionnaireRepository`,
  `ExerciseRepository`, `WorkoutRepository`), each combining a Retrofit `ApiService` with a Room
  DAO. `WorkoutRepository` also depends on `FitnessAgent` to generate recommendations alongside
  persisted data.
- `data/local/` — Room entities/DAOs, plus `TokenManager` (JWT storage in
  EncryptedSharedPreferences/DataStore).
- `data/remote/` — Retrofit `ApiService` + DTOs; `RetrofitClient` wires in `TokenManager` for
  auth headers.
- `ui/navigation/NavGraph.kt` — single `NavHost` wiring every screen; ViewModels are constructed
  per-route via `viewModelFactory { initializer { ... } }` pulling dependencies out of
  `AppContainer` (no ViewModel-level DI framework either). Auth state (`currentUserId`,
  logged-in check via `container.authRepository.isLoggedIn()`) lives in this composable and
  decides the start destination.
- `ui/<feature>/` — one package per feature area (`auth`, `dashboard`, `exercises`,
  `nutrition`, `questionnaire`, `workout`), each holding its Compose screens + ViewModel.

### Backend (`backend/`)

Django project (`config/`) with one app per domain, matching the client's feature areas:
`apps/users`, `apps/questionnaire`, `apps/exercises`, `apps/workouts`. Each follows standard
DRF layout (`models.py`, `serializers.py`, `views.py`, `urls.py`, `migrations/`). All routes are
versioned under `/api/v1/`. Auth is JWT via `djangorestframework-simplejwt`. Questionnaire
endpoints are restricted to their own owner (`apps/questionnaire/views.py`). Both
`DJANGO_SECRET_KEY` and `POSTGRES_PASSWORD` are required env vars — the app will not start with
hardcoded defaults.

## Conventions (from AGENT.md)

- Kotlin: camelCase naming; one reusable Compose component per responsibility.
- No plaintext passwords anywhere; hashing is backend-only (Django `PBKDF2`), JWTs stored
  encrypted client-side; HTTPS-only in production (`usesCleartextTraffic=false`).
- Validate questionnaire inputs on both client and backend.
- Minimum test coverage target: 80% (JUnit/MockK on Android, pytest on backend).
