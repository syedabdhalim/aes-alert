# AES Alert Tracker

Android app that warns drivers when approaching AES/AWAS speed cameras on Malaysian highways. Runs as a background GPS service with real-time proximity detection and audio/visual alerts.

Built for personal use. Mount your phone on the dashboard, launch the app, and drive.

## Features

- Real-time GPS tracking via foreground service
- Proximity detection using Haversine distance + bearing-aware filtering
- Full-screen alert overlay when approaching a camera (speed limit, current speed, distance)
- Audio alert: 3-beep sequence on approach
- Dashboard showing current speed, GPS status, and nearest camera info
- 29 AES/AWAS cameras across Malaysian highways (PLUS E1/E2, ELITE, GCE, SKVE, LEKAS, LPT2, federal routes)
- Configurable alert distance (500m - 5km)
- Camera database with edit capability
- Route simulator for testing without driving
- Auto-start on device boot
- Landscape-only, keep-screen-on mode

## How It Works

```
GPS Location Update
        |
        v
  ProximityEngine
   - Haversine distance to each camera
   - Bearing match (is user traveling in the camera's monitored direction?)
   - Camera-ahead check (is the camera in front, not behind?)
        |
        v
  Alert State Machine
   CLEAR -> APPROACHING -> PASSING -> PASSED -> (30s cooldown) -> CLEAR
        |
        v
  AlertManager (audio)  +  AlertOverlay (visual)
```

The app only alerts when:
1. You are within the scan radius (2x alert distance)
2. Your travel direction matches the camera's monitored direction (within tolerance)
3. The camera is ahead of you, not behind

## Camera Coverage

| Highway | Cameras | States |
|---------|---------|--------|
| PLUS E2 (Southern) | 9 | Johor, Melaka, Selangor |
| PLUS E1 (Northern) | 8 | Perak, Pulau Pinang, Kedah, Selangor |
| ELITE E6 | 2 | Selangor |
| GCE E35 | 2 | Selangor |
| SKVE E26 | 1 | Selangor |
| LEKAS E21 | 2 | Negeri Sembilan |
| Lebuh Sentosa | 1 | WP Putrajaya |
| Federal Route 1 | 1 | Perak |
| Federal Route 8 | 1 | Kelantan |
| LPT2 E8 | 2 | Terengganu |

Sources: JPJ official AES/AWAS list, OpenStreetMap Overpass API, community reports.

## Architecture

```
com.syed.aesalert/
  |
  +-- data/
  |     AESCamera.kt          Room entity (lat, lng, speed limit, bearing, direction)
  |     CameraDao.kt          Room DAO (CRUD operations)
  |     AESDatabase.kt        Room database with auto-seeding
  |     CameraData.kt         Hardcoded camera list (29 cameras)
  |     AppSettings.kt        SharedPreferences wrapper (alert distance)
  |
  +-- logic/
  |     ProximityEngine.kt    Core detection: Haversine + bearing match + ahead check
  |     AlertManager.kt       Audio alerts via ToneGenerator
  |     RouteSimulator.kt     Fake GPS routes for testing (Kajang, JB)
  |
  +-- service/
  |     LocationTrackingService.kt   Foreground service, GPS updates, state flow
  |
  +-- ui/
  |     MainScreen.kt         Dashboard with speed display + camera info
  |     AlertOverlay.kt       Full-screen warning when approaching camera
  |     CameraListScreen.kt   View/edit camera database
  |     SettingsDialog.kt     Alert distance configuration
  |     theme/Theme.kt        Dark theme (Material 3)
  |
  +-- MainActivity.kt         Entry point, permissions, service binding
  +-- BootReceiver.kt         Auto-start on device boot
```

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Database**: Room (SQLite)
- **Concurrency**: Kotlin Coroutines + StateFlow
- **Build**: Gradle (Kotlin DSL), KSP
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35

## Build

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

Requires Android Studio with SDK 35 installed.

## Permissions

| Permission | Reason |
|------------|--------|
| `ACCESS_FINE_LOCATION` | GPS tracking |
| `ACCESS_BACKGROUND_LOCATION` | Keep tracking when screen is off |
| `FOREGROUND_SERVICE` | Persistent GPS service |
| `POST_NOTIFICATIONS` | Foreground service notification (Android 13+) |
| `RECEIVE_BOOT_COMPLETED` | Auto-start on boot |
| `WAKE_LOCK` | Keep screen on while driving |

## Disclaimer

This app is for informational purposes only. It is the driver's responsibility to obey all traffic laws and speed limits at all times. Camera data may be outdated or inaccurate.

## License

Personal project. Not licensed for redistribution.
