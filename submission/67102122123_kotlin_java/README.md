# 67102122123 Kotlin + Java Source Code

ชื่อ: ภัทรธิดา ดอกงาม  
รหัสนักศึกษา: 67102122123

## ตำแหน่งโค้ดแต่ละข้อ

- ข้อ 1 Activity Lifecycle: `com/example/myapplication/MainActivity.kt`
  - Lifecycle methods: `onCreate`, `onStart`, `onResume`, `onPause`, `onStop`, `onDestroy`
  - หน้าจอ: `ProfileScreen` และ `LifecycleRow`
- ข้อ 2 State Hoisting และแผนที่: `com/example/myapplication/MainActivity.kt`
  - หน้าจอ: `MapScreen`, `CoordField` และ `EmbeddedMap`
- ข้อ 3 MVVM คำนวณค่างวด:
  - View: `com/example/myapplication/MainActivity.kt` (`InstallmentScreen`)
  - ViewModel: `com/example/myapplication/InstallmentViewModel.kt`
  - Model: `com/example/myapplication/InstallmentModel.kt`
- Theme: `com/example/myapplication/ui/theme/`

## Dependency สำหรับแผนที่

เพิ่มใน `dependencies` ของ `app/build.gradle.kts`:

```kotlin
implementation("org.osmdroid:osmdroid-android:6.1.20")
```

และเพิ่ม permission ใน `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```
