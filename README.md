# Learning Dashboard

A small Android app (Kotlin, Jetpack Compose, Material 3) with three screens: **Login → Course Dashboard → Course Details**. MVVM + Repository, Room (offline cache), Hilt, Navigation Compose.

**Run:** open in Android Studio and Run, or `./gradlew assembleDebug`. Sign in with `demo@learning.app` / `demo1234`.

## 1. Architecture

MVVM + Repository was chosen to keep a strict, testable separation between UI, state, and data:

- **UI** (`presentation/*`) — composables render immutable, sealed UI states (`Loading/Success/Empty/Error`); zero business logic.
- **ViewModel** — one per screen; exposes `StateFlow`, collected with `collectAsStateWithLifecycle()` (lifecycle-aware, no leaks). One-way data flow: events go down, state comes up.
- **Repository** (`domain/repository` interfaces, `data/repository` implementations) — the single source of truth for the UI. It coordinates a remote data source (mock API behind a `CourseApi`/`AuthApi` interface) and a local data source (Room). ViewModels never know where data came from.
- **Domain** — pure Kotlin: models, `ProgressCalculator`, validation rules. No Android dependencies, unit-testable.
- **Hilt DI** — modules bind interfaces to implementations; swapping the fake API for Retrofit is a one-module change.

DTO → Entity → Domain mappings are explicit (`data/mapper`). Business rules never live in composables or the database layer.

## 2. Offline Support

- `refreshCourses()` fetches remotely; on success the response is persisted to Room (`courses` + `lessons` tables) in a transaction, and the UI **always reads from Room** via `Flow`.
- On remote failure the repository logs the error and returns `Result.failure`; the ViewModel keeps showing cached Room content with a *"Couldn't refresh. Showing saved courses."* banner. Only when remote fails **and** the cache is empty is the Error state (with Retry) shown.
- Lesson completion is written to Room; course progress is recalculated as `completedLessons / totalLessons * 100` (division-by-zero safe) in the same transaction, so updated progress appears on the Dashboard and survives app restarts.
- Refresh re-derives progress from stored lessons, so cached data is never clobbered by the server.
- **Simulating failure (demo):** debug builds show a dashboard top-bar icon cycling `Normal → Failure → Empty` (persisted across restarts). Select *Failure*, tap refresh or relaunch the app → cached courses remain. The bundled fake API reads local assets, so airplane mode alone does not affect it.

## 3. Security

This assignment uses a mock login with no real tokens. In production: store access/refresh tokens only in **Android Keystore**-backed storage (e.g. EncryptedSharedPreferences or a Keystore-wrapped DB), never plain SharedPreferences; keep tokens short-lived and rotate refresh tokens; use HTTPS/TLS exclusively; never log credentials or tokens (logs here contain only exception messages).

## 4. Scale (1M users, hundreds of courses)

1. **Server-side pagination + delta sync** — replace full-list fetch with paged endpoints and incremental sync (updated-since) to cut payload and startup time.
2. **WorkManager background sync** — reliable, battery-aware refresh with retry/backoff, independent of screen lifecycle.
3. **DB indexing & query optimization** — indices on foreign keys/joins, `PagingSource` for large course lists, migration tests.
4. **API caching/CDN + observability** — HTTP caching/ETags, CDN for thumbnails (Coil with memory+disk cache), crash reporting, analytics, feature flags for gradual rollout.
5. **Modularization** — split into `:feature:*`, `:core:*` modules for parallel builds and ownership at team scale.

## 5. Second Platform (iOS/macOS equivalent)

SwiftUI for UI; `@Observable` view models mirroring this MVVM structure; `async/await` + structured concurrency for API calls; `URLSession` as the remote data source; SwiftData/Core Data as the Room equivalent with observation; Keychain for tokens; a repository/service layer exposing observable cached data; `NavigationStack` for login → dashboard → details routing.
