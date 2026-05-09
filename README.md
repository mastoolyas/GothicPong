# Gothic Pong

**Gothic Pong** is a stylized, one-player vertical arcade game built for Android. Unlike traditional Pong, your goal isn't just to stay alive—it's to climb. Control two paddles simultaneously to launch the ball upward into the infinite dark space of the gothic realm.

![Gothic Pong Logo](app/src/main/res/mipmap-xhdpi/ic_launcher.png)

## 🎮 Gameplay Features

- **Dual-Paddle Control**: Use multi-touch to move both left and right paddles.
- **Infinite Climbing**: No ceiling! The camera follows the ball as you reach new heights.
- **Dynamic Difficulty**: The ball gains **15% speed every 1000 points**. Look out for the "SPEED UP!" notification.
- **Skill-Based Physics**: 
  - Hit the **top half** of a paddle for a steep upward launch (91-160°).
  - Hit the **bottom half** for a defensive downward recovery (45-90°).
- **Procedural Environment**: A dynamic starfield and altitude markers generate as you climb.
- **Play Games Integration**: Sign in to see your name and save your high scores to the cloud.

## 🛠 Tech Stack

- **Language**: 100% Kotlin
- **Rendering**: Custom `SurfaceView` with a dedicated game loop thread for smooth performance.
- **Logic**: Decoupled `GameEngine` for high testability and predictable physics.
- **Persistence**: `SharedPreferences` for local high score tracking.
- **SDKs**: 
  - Android SDK (Min API 24, Target API 34)
  - Google Play Games Services v2

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana or newer.
- Android SDK 34.

### Installation
1. Clone the repository.
2. Ensure the following assets are present:
   - `app/src/main/res/drawable/menu_bg.png` (Title screen)
   - `app/src/main/res/mipmap-xhdpi/ic_launcher.png` (App icon)
3. Sync the project with Gradle.
4. Run on a physical device or emulator.

## 🧪 Testing

The project includes a comprehensive suite of unit tests covering ball physics, camera scrolling, scoring, and difficulty scaling.

Run tests via terminal:
```bash
./gradlew :app:testDebugUnitTest
```

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.
