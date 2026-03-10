# AES Alert Tracker - Architecture Documentation

## Overview

AES Alert Tracker is a fully offline Android application that alerts drivers when approaching AES (Automated Enforcement System) speed cameras on Malaysian highways. Designed for Android head units, it uses GPS proximity detection with directional awareness to trigger alerts only when the driver is heading towards a camera.

## System Architecture

```
+-------------------------------------------------------------------+
|                        MainActivity                                |
|  - Permission handling                                             |
|  - Service binding                                                 |
|  - Screen navigation (Dashboard / Camera List)                     |
|  - Settings & Edit dialogs                                         |
+----------------------------+--------------------------------------+
                             |
                    Service Binding (Binder)
                             |
+----------------------------v--------------------------------------+
|                  LocationTrackingService                           |
|  - Foreground Service (persistent notification)                    |
|  - GPS location updates (LocationManager.GPS_PROVIDER)             |
|  - Delegates to ProximityEngine on each GPS tick                   |
|  - Delegates to AlertManager for audio feedback                    |
|  - Exposes LocationState via StateFlow                             |
|  - Hosts RouteSimulator for testing                                |
+----------+-----------------+------------------+-------------------+
           |                 |                  |
           v                 v                  v
   +-------+------+  +------+-------+  +-------+---------+
   | ProximityEngine|  | AlertManager |  | RouteSimulator  |
   | - Haversine    |  | - ToneGen    |  | - Fake GPS      |
   | - Bearing calc |  | - Beep x3    |  |   points        |
   | - State machine|  |              |  | - 2 routes      |
   | - Direction    |  |              |  |   (Kajang, JB)  |
   |   matching     |  |              |  |                 |
   +-------+--------+  +--------------+  +-----------------+
           |
           v
   +-------+--------+
   |   Room Database |
   |  (aes_cameras)  |
   |  - 29 cameras   |
   |  - Pre-seeded   |
   +-------+---------+
           |
           v
   +-------+---------+
   |   CameraData.kt |
   |  (Default seed)  |
   |  - GPS coords    |
   |  - Bearing angles|
   |  - Speed limits  |
   +------------------+
```

## Data Flow

### GPS Update Cycle (runs every 1 second)

```
GPS Satellite
    |
    v
LocationManager.GPS_PROVIDER
    |
    v
LocationTrackingService.onLocationChanged(location)
    |
    +-- Extract: lat, lon, speed, bearing
    |
    +-- Fallback bearing calculation (if GPS bearing = 0)
    |       Uses: previous location -> current location vector
    |
    v
ProximityEngine.checkProximity(lat, lon, bearing, speed)
    |
    +-- For each of 29 cameras:
    |       1. Haversine distance check (within scan radius?)
    |       2. Direction match (user bearing ~= camera bearing?)
    |       3. Camera ahead check (bearing TO camera ~= travel direction?)
    |
    +-- State machine evaluation:
    |       CLEAR -> APPROACHING (within alert radius + direction match)
    |       APPROACHING -> PASSING (within 200m)
    |       PASSING -> PASSED (camera now behind user)
    |       PASSED -> CLEAR (after 30s cooldown)
    |
    v
CameraAlert { state, camera, distance, speed }
    |
    +---> AlertManager.handleAlert()
    |       - APPROACHING: play beep x3 (600ms intervals)
    |       - PASSING/PASSED/CLEAR: no audio
    |
    +---> LocationState (StateFlow)
            |
            v
        Jetpack Compose UI
            |
            +-- CLEAR: Dashboard (speed, GPS status, nearest camera info)
            +-- APPROACHING/PASSING: Full-screen alert overlay
            +-- PASSED: Dashboard with "--" distance
```

### Direction Detection Algorithm

The core differentiator - alerts only fire when driving TOWARDS a camera.

```
Step 1: Direction Match
    User bearing: 350 (heading north-northwest)
    Camera bearing: 350 (monitors northbound traffic)
    Tolerance: 60 degrees
    |350 - 350| = 0 < 60  -->  MATCH

Step 2: Camera Ahead Check
    User position: (2.960, 101.743)
    Camera position: (2.974, 101.742)
    Bearing from user to camera: ~355 (camera is north of user)
    User travel bearing: 350
    |350 - 355| = 5 < 90  -->  CAMERA IS AHEAD

Step 3: After Passing
    User position: (2.990, 101.741)  [now north of camera]
    Camera position: (2.974, 101.742)
    Bearing from user to camera: ~175 (camera is now south/behind)
    User travel bearing: 350
    |350 - 175| = 175 > 90  -->  CAMERA IS BEHIND  -->  PASSED
```

### Bearing Math Reference

```
         0 / 360
           N
           |
    315  NW | NE  45
           \|/
  270 W ---+--- E 90
           /|\
    225  SW | SE  135
           |
           S
         180
```

## Database Schema

### aes_cameras table

| Column           | Type    | Description                                   |
|------------------|---------|-----------------------------------------------|
| id               | INTEGER | Primary key, auto-increment                   |
| name             | TEXT    | Camera display name (e.g., "KM301.6 PLUS Kajang") |
| latitude         | REAL    | GPS latitude                                  |
| longitude        | REAL    | GPS longitude                                 |
| speedLimit       | INTEGER | Speed limit in km/h (70/80/90/110)            |
| bearingAngle     | REAL    | Direction camera monitors (0-360 degrees)     |
| bearingTolerance | REAL    | Matching tolerance (default 60 degrees)       |
| direction        | TEXT    | Display text ("Utara", "Selatan", etc.)       |
| state            | TEXT    | Malaysian state (Negeri)                      |
| highway          | TEXT    | Highway name ("PLUS E1", "ELITE E6", etc.)    |

### Data Sources

- GPS coordinates: OpenStreetMap Overpass API (highway=speed_camera nodes)
- Camera list: JPJ official AES/AWAS enforcement list (2024-2025)
- Bearing angles: Calculated from highway alignment at each KM marker

## State Machine

```
                    +----------+
                    |  CLEAR   |<------ (30s cooldown expired)
                    +----+-----+             |
                         |                   |
          (within scan radius          +-----+-----+
           + direction match           |  PASSED   |
           + camera ahead)             +-----+-----+
                         |                   ^
                    +----v-----+             |
                    |APPROACHING|-----> (camera now behind user)
                    +----+-----+             |
                         |                   |
                  (within 200m)              |
                         |                   |
                    +----v-----+             |
                    | PASSING  |-------------+
                    +----------+   (camera behind after passing)
```

## Configurable Parameters

| Parameter       | Default | Storage          | Options              |
|-----------------|---------|------------------|----------------------|
| Alert distance  | 1000m   | SharedPreferences | 500m/1km/2km/3km/5km |
| Scan radius     | 2x alert| Derived          | Auto-calculated      |
| Passing radius  | 200m    | Hardcoded        | -                    |
| Cooldown        | 30s     | Hardcoded        | -                    |
| Bearing tolerance| 60 deg | Per-camera (DB)  | Editable             |
| GPS interval    | 1000ms  | Hardcoded        | -                    |
| GPS min distance| 10m     | Hardcoded        | -                    |

## Project Structure

```
app/src/main/java/com/aesalert/app/
|
+-- MainActivity.kt              # Entry point, permissions, navigation
+-- BootReceiver.kt              # Auto-start on device boot
|
+-- data/
|   +-- AESCamera.kt             # Room Entity
|   +-- CameraDao.kt             # Room DAO (queries)
|   +-- AESDatabase.kt           # Room Database singleton
|   +-- CameraData.kt            # Pre-seed data (29 cameras)
|   +-- AppSettings.kt           # SharedPreferences wrapper
|
+-- service/
|   +-- LocationTrackingService.kt  # Foreground GPS service
|
+-- logic/
|   +-- ProximityEngine.kt       # Distance + direction + state machine
|   +-- AlertManager.kt          # Audio alert (ToneGenerator)
|   +-- RouteSimulator.kt        # Fake GPS routes for testing
|
+-- ui/
    +-- MainScreen.kt            # Dashboard layout
    +-- AlertOverlay.kt          # Full-screen camera warning
    +-- CameraListScreen.kt      # Camera list + edit
    +-- SettingsDialog.kt        # Alert distance config
    +-- theme/Theme.kt           # Dark high-contrast theme
```

## Key Design Decisions

### 1. LocationManager over Google Fused Location
Android head units typically lack Google Play Services certification. Using LocationManager.GPS_PROVIDER directly ensures compatibility across all Android devices without external dependencies.

### 2. Foreground Service over WorkManager
GPS tracking must be continuous and real-time. WorkManager is for deferrable background work. A foreground service with persistent notification ensures the OS won't kill the tracking process.

### 3. Pre-seeded Database over API
Fully offline operation. No server dependency, no network latency, no data costs. Camera locations change infrequently (government gazette updates), so pre-seeded data with manual edit capability is the right tradeoff.

### 4. ToneGenerator over MediaPlayer
No dependency on bundled audio files. ToneGenerator is guaranteed available on all Android devices. Simpler, more reliable, smaller APK.

### 5. Bearing-based Direction Detection
Simple GPS proximity alerts (distance-only) would fire for both directions on a highway. Adding bearing comparison ensures alerts only trigger when approaching the camera, not when driving the opposite direction past it.

## Build & Deploy

- **Min SDK**: 26 (Android 8.0+)
- **Target SDK**: 35 (Android 15)
- **Build**: Gradle 8.11.1, Kotlin 2.1.0, AGP 8.7.3
- **APK**: Build via Android Studio > Build > Build APK
- **Install**: Sideload via ADB or file manager on head unit

## Camera Coverage

29 cameras across 10 highways in Peninsular Malaysia:

| Highway              | Count | States                          |
|----------------------|-------|---------------------------------|
| PLUS E2 (Southern)   | 9     | Johor, Melaka, Selangor         |
| PLUS E1 (Northern)   | 8     | Perak, Pulau Pinang, Kedah, Selangor |
| ELITE E6             | 2     | Selangor                        |
| GCE E35 (Guthrie)    | 2     | Selangor                        |
| LEKAS E21            | 2     | Negeri Sembilan                 |
| LPT2 E8 (East Coast) | 2     | Terengganu                      |
| SKVE E26             | 1     | Selangor                        |
| Lebuh Sentosa        | 1     | WP Putrajaya                    |
| Jalan Persekutuan 1  | 1     | Perak                           |
| Jalan Persekutuan 8  | 1     | Kelantan                        |
