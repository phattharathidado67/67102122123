# รายงานการพัฒนาแอปพลิเคชัน Android สมัยใหม่

## ระบบ Activity Lifecycle, แผนที่แบบ State Hoisting และเครื่องคำนวณค่างวดแบบ MVVM

ผู้จัดทำ: **ภัทรธิดา ดอกงาม**  
รหัสนักศึกษา: **67102122123**  
Repository: <https://github.com/phattharathidado67/67102122123>

---

## 1. บทคัดย่อ

โครงงานนี้เป็นแอปพลิเคชัน Android ที่พัฒนาด้วยภาษา Kotlin และ Jetpack Compose ประกอบด้วยงานตามข้อสอบกลางภาค 3 ส่วน ได้แก่

1. หน้าตรวจสอบวงจรชีวิตของ Activity ซึ่งแสดงภาพถ่ายจริงของนักศึกษา และแจ้งชื่อเมทอด Lifecycle ด้วย Toast
2. ฟอร์มรับพิกัด Latitude และ Longitude ที่ออกแบบตามหลัก State Hoisting และ Unidirectional Data Flow พร้อมเปิดแอปแผนที่ภายนอกด้วย Implicit Intent
3. เครื่องคำนวณค่างวดแบบดอกเบี้ยคงที่ ซึ่งแยกโครงสร้าง Model, View และ ViewModel ตามสถาปัตยกรรม MVVM

นอกเหนือจากข้อกำหนดพื้นฐาน แอปยังมีแผนที่ OpenStreetMap แบบโต้ตอบภายในหน้าจอ ผู้ใช้สามารถลาก ซูม และปักหมุดจากพิกัดที่กรอกได้ โดยยังคงปุ่มเปิดแอปแผนที่ภายนอกไว้ตามโจทย์

---

## 2. เทคโนโลยีที่ใช้

- ภาษา Kotlin
- Android Studio และ Gradle
- Jetpack Compose สำหรับสร้าง UI แบบ Declarative
- Material 3 สำหรับคอมโพเนนต์และระบบสี
- Lifecycle ViewModel สำหรับเก็บ UI State ข้ามการหมุนหน้าจอ
- Android Intent และ URI แบบ `geo:` สำหรับเปิดแผนที่ภายนอก
- osmdroid MapView และ OpenStreetMap สำหรับแผนที่จริงภายในแอป
- JUnit สำหรับทดสอบสูตรและ ViewModel
- Git และ GitHub สำหรับ Version Control

---

## 3. โครงสร้างโปรเจกต์

```text
app/src/main/
├── AndroidManifest.xml
├── res/drawable/student_photo.jpg  ภาพถ่ายจริงของนักศึกษา
├── res/drawable/student_photo.jpg  ภาพถ่ายจริงของนักศึกษา
└── java/com/example/myapplication/
    ├── MainActivity.kt             Activity และ View ของทั้ง 3 ข้อ
    ├── InstallmentModel.kt         Model และสูตรคำนวณ
    ├── InstallmentViewModel.kt     State และตัวกลางระหว่าง View กับ Model
    └── ui/theme/                   สีและ Theme ของแอป

app/src/test/
└── java/com/example/myapplication/
    ├── InstallmentCalculatorTest.kt
    └── InstallmentViewModelTest.kt
```

การแบ่งหน้าที่สำคัญคือ `MainActivity.kt` รับผิดชอบสิ่งที่ผู้ใช้เห็น, `InstallmentViewModel.kt` รับผิดชอบ State และ Event และ `InstallmentModel.kt` รับผิดชอบ Business Logic

---

## 4. ภาพรวมการทำงานของแอป

เมื่อแอปเริ่มทำงาน `MainActivity.onCreate()` จะเรียก `setContent` เพื่อวาด `ToolkitApp()` ภายใน `MyApplicationTheme` หน้าหลักใช้ `NavigationBar` แบ่งเป็น 3 แท็บ:

- Lifecycle
- แผนที่
- ค่างวด

ตัวแปร `page` ใช้ `rememberSaveable` จึงจำแท็บที่เลือกได้เมื่อ Activity ถูกสร้างใหม่จากการหมุนจอ จากนั้น `when (page)` เลือกแสดงหน้าที่สัมพันธ์กับแท็บ

```kotlin
var page by rememberSaveable { mutableIntStateOf(0) }

when (page) {
    0 -> ProfileScreen(...)
    1 -> MapScreen()
    else -> InstallmentScreen()
}
```

---

# ส่วนที่ 1: Activity Lifecycle และ Version Control

## 5. การออกแบบหน้าจอแรก

ภาพจริงถูกเก็บใน Resource Drawable ชื่อ `student_photo.jpg` และแสดงด้วย Composable `Image`

```kotlin
Image(
    painter = painterResource(R.drawable.student_photo),
    contentDescription = "ภาพถ่ายนักศึกษา",
    contentScale = ContentScale.Crop,
    modifier = Modifier.size(210.dp).clip(CircleShape)
)
```

คำอธิบายแต่ละส่วน:

- `painterResource(...)` อ่านภาพจาก Resource Drawable ไม่ได้อ่านจาก path ภายนอกเครื่อง
- `contentDescription` ให้คำอธิบายภาพสำหรับ Accessibility
- `ContentScale.Crop` ขยายภาพให้เต็มพื้นที่โดยรักษาสัดส่วน
- `size(210.dp)` กำหนดขนาดภาพ
- `clip(CircleShape)` ตัดภาพเป็นวงกลม
- ใช้ `Card`, `Surface`, `Column`, `Row` และ `Spacer` จัด Layout ด้วย Compose

## 6. เมทอด Activity Lifecycle ทั้ง 6 ขั้นตอน

`MainActivity` สืบทอดจาก `ComponentActivity` และ Override เมทอดครบตามโจทย์

### 6.1 `onCreate()`

ถูกเรียกเมื่อ Activity ถูกสร้างครั้งแรก หรือถูกสร้างใหม่หลังหมุนจอ ทำหน้าที่แจ้ง Toast, เปิด Edge-to-Edge และกำหนด Compose UI

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    showLifecycleToast("onCreate()")
    enableEdgeToEdge()
    setContent { MyApplicationTheme { MidtermApp() } }
}
```

### 6.2 `onStart()`

ถูกเรียกเมื่อ Activity เริ่มปรากฏให้ผู้ใช้เห็น

```kotlin
override fun onStart() {
    super.onStart()
    showLifecycleToast("onStart()")
}
```

### 6.3 `onResume()`

ถูกเรียกเมื่อ Activity อยู่ด้านหน้าและพร้อมรับการโต้ตอบ

### 6.4 `onPause()`

ถูกเรียกเมื่อ Activity กำลังเสียโฟกัส เช่น มีหน้าจออื่นเข้ามาบัง หรือกำลังหมุนจอ

### 6.5 `onStop()`

ถูกเรียกเมื่อ Activity ไม่ปรากฏให้ผู้ใช้เห็น

### 6.6 `onDestroy()`

ถูกเรียกก่อน Activity ถูกทำลาย เช่น เมื่อเกิด Configuration Change จากการหมุนจอ

## 7. การแสดง Toast และ Logcat

ทุกเมทอดเรียกฟังก์ชันกลาง `showLifecycleToast()` เพื่อลดโค้ดซ้ำ

```kotlin
private fun showLifecycleToast(methodName: String) {
    Log.d(lifecycleTag, methodName)
    Toast.makeText(applicationContext, methodName, Toast.LENGTH_SHORT).show()
}
```

- `applicationContext` ตรงตามข้อกำหนด และไม่ผูก Toast กับอายุของ Activity โดยไม่จำเป็น
- `Toast.LENGTH_SHORT` แสดงข้อความช่วงสั้น
- `.show()` สั่งแสดง Toast จริง หากลืมเรียก เมทอดนี้ Toast จะไม่ปรากฏ
- `Log.d` ช่วยตรวจลำดับเดียวกันใน Logcat โดยใช้ Tag `MidtermLifecycle`

ลำดับเมื่อเปิดแอปตามปกติ:

```text
onCreate() → onStart() → onResume()
```

ลำดับสำคัญเมื่อหมุนจอ:

```text
onPause() → onStop() → onDestroy()
→ onCreate() → onStart() → onResume()
```

## 8. Version Control

โครงงานเชื่อมต่อกับ GitHub Repository และ Push ขึ้น Branch `main` แล้ว โดย Commit ปัจจุบัน ได้แก่

```text
fdcbc5f first commit
7f7a33c feat: add completed Android assignment source
```

Source code ล่าสุดถูก Push ไปยัง GitHub Repository ของผู้จัดทำแล้ว

---

# ส่วนที่ 2: แผนที่แบบ State Hoisting

## 9. แนวคิด State Hoisting

State Hoisting คือการย้าย State ออกจาก Composable ที่วาด UI ไปไว้ใน Composable ระดับบน ทำให้ Composable ลูกเป็น Stateless และนำกลับมาใช้หรือทดสอบได้ง่าย

```text
ผู้ใช้พิมพ์/กดปุ่ม
        ↓ Event callback
MapScreen (Stateful) เปลี่ยน State
        ↓ ส่งค่าผ่าน Parameters
MapScreen วาด UI ใหม่
```

นี่คือ Unidirectional Data Flow:

- State ไหลลงจาก Parent ไป Child
- Event ไหลขึ้นจาก Child กลับไป Parent

## 10. Stateful Composable: `MapScreen()`

`MapScreen()` เป็นเจ้าของ State และ Logic

```kotlin
var latitude by rememberSaveable { mutableStateOf("") }
var longitude by rememberSaveable { mutableStateOf("") }
var mapLatitude by rememberSaveable { mutableStateOf("17.1899") }
var mapLongitude by rememberSaveable { mutableStateOf("104.0914") }
```

- `mutableStateOf` ทำให้ Compose สังเกตการเปลี่ยนค่าและ Recompose UI
- `rememberSaveable` บันทึก String ลง Saved Instance State/Bundle
- เมื่อหมุนจอ ค่าในช่องกรอกและตำแหน่งหมุดจึงไม่หาย

`latitude` และ `longitude` คือข้อความในช่องกรอก ส่วน `mapLatitude` และ `mapLongitude` คือตำแหน่งที่แผนที่กำลังแสดง การแยกสองชุดทำให้แผนที่เปลี่ยนเฉพาะเมื่อผู้ใช้กดปุ่ม ไม่เลื่อนทุกครั้งที่พิมพ์ตัวเลขหนึ่งตัว

## 11. การตรวจสอบข้อมูลพิกัด

ฟังก์ชัน `valid()` แปลง String เป็น Double ด้วย `toDoubleOrNull()` ซึ่งปลอดภัยกว่าการใช้ `toDouble()` เพราะข้อมูลผิดจะให้ค่า `null` แทนการ Crash

เงื่อนไขตรวจสอบมีดังนี้:

- ต้องกรอกครบทั้ง Latitude และ Longitude
- Latitude ต้องอยู่ระหว่าง -90 ถึง 90
- Longitude ต้องอยู่ระหว่าง -180 ถึง 180

ถ้าข้อมูลผิด ฟังก์ชันแสดง Toast และคืน `null` จึงไม่มีการสร้าง Intent

## 12. Stateless Composable: ส่วนฟอร์มภายใน `MapScreen()`

ช่องกรอกรับค่า State จาก `MapScreen()` และส่ง Event กลับผ่าน Callback ของ `CoordField()` ทำให้ State มีเจ้าของเพียงจุดเดียว

```kotlin
private fun CoordField(
    value: String,
    change: (String) -> Unit,
    label: String,
    hint: String,
    modifier: Modifier
)
```

`CoordField` ไม่เก็บ State เอง แต่รับ `value` และส่งค่าที่ผู้ใช้พิมพ์กลับผ่าน `change` ส่วนปุ่มใน `MapScreen()` ตรวจสอบข้อมูลและอัปเดตพิกัดที่แสดง

## 13. Implicit Intent ตามข้อสอบ

ปุ่ม “เปิดแอปแผนที่” สร้าง Intent แบบไม่ระบุชื่อแอปปลายทาง

```kotlin
val uri = "geo:0,0?q=" + shownLat + "," + shownLng
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
```

- `ACTION_VIEW` หมายถึงขอให้ระบบเปิดดูข้อมูล
- `geo:lat,lng` คือ URI Scheme สำหรับพิกัด
- `resolveActivity()` ตรวจว่ามีแอปรองรับหรือไม่
- ถ้ามีจึงเรียก `startActivity(intent)`
- ถ้าไม่มีจะแสดง Toast แทน ทำให้ไม่ Crash

ใน `AndroidManifest.xml` มี `<queries>` สำหรับ `ACTION_VIEW` และ Scheme `geo` เพราะ Android 11 ขึ้นไปจำกัดการมองเห็นแอปอื่น

## 14. แผนที่ภายในแอป

ฟังก์ชัน `EmbeddedMap()` ใช้ `AndroidView` นำ native `MapView` ของ osmdroid มาใช้ใน Compose โหลดแผนที่ OpenStreetMap ที่มีชื่อถนนและสถานที่ พร้อมสร้าง Marker ที่พิกัดที่เลือก

```text
Compose AndroidView
       ↓
osmdroid MapView
       ↓
OpenStreetMap tile provider
       ↓
OpenStreetMap Tile Server
```

เมื่อพิกัดเปลี่ยน Compose จะสร้าง MapView สำหรับ `GeoPoint` ใหม่ ตั้ง Zoom ระดับ 16 เลื่อนศูนย์กลาง และเพิ่ม Marker ตรงตำแหน่ง โดย `onRelease` เรียก `onDetach()` เพื่อคืนทรัพยากรและป้องกัน memory leak

ผู้ใช้สามารถ:

- ลากแผนที่
- ใช้ pinch gesture เพื่อซูม
- แตะ Marker เพื่อดูพิกัด
- กรอกพิกัดใหม่แล้วกด “แสดงตำแหน่งบนแผนที่” เพื่อเลื่อนกลับไปยัง Marker ใหม่
- กด “เปิดแอปแผนที่” เพื่อสาธิต Implicit Intent ตามโจทย์

Permission `INTERNET` ใช้ดาวน์โหลด Tile ของ OpenStreetMap และแอปตั้ง User-Agent เฉพาะพร้อมแสดงเครดิต © OpenStreetMap contributors ตามนโยบายผู้ให้บริการ

---

# ส่วนที่ 3: เครื่องคำนวณค่างวดแบบ MVVM

## 15. แนวคิด MVVM

โครงงานแบ่งความรับผิดชอบดังนี้:

```text
View: InstallmentScreen()
  │ รับการพิมพ์และการกดปุ่ม
  ↓
ViewModel: InstallmentViewModel
  │ เก็บ UI State และรับ Event
  ↓
Model: InstallmentCalculator
  │ คำนวณ Business Logic
  ↓
InstallmentResult → ViewModel → View แสดงผล
```

### Model

อยู่ใน `InstallmentModel.kt` มี `InstallmentResult` สำหรับเก็บผล และ `InstallmentCalculator` สำหรับคำนวณ โดยไม่ import Android หรือ Compose จึงเป็น Pure Kotlin และทดสอบง่าย

### View

คือ `InstallmentScreen()` ใน `MainActivity.kt` มีหน้าที่วาด TextField, Slider, Button และ Result Card ไม่คำนวณสูตรเอง

### ViewModel

อยู่ใน `InstallmentViewModel.kt` ทำหน้าที่เก็บ `InstallmentUiState`, รับ Event จาก View, ตรวจราคาสินค้า และเรียก Model

## 16. UI State

```kotlin
data class InstallmentUiState(
    val price: String = "",
    val monthlyRate: Float = 1f,
    val months: Int = 12,
    val result: InstallmentResult? = null
)
```

- `price` เก็บข้อความราคาสินค้า
- `monthlyRate` เก็บอัตราดอกเบี้ยต่อเดือน ค่าเริ่มต้น 1%
- `months` เก็บจำนวนเดือน ค่าเริ่มต้น 12 เดือน
- `result` เป็น Nullable เพราะก่อนกดคำนวณยังไม่มีผลลัพธ์

ViewModel ประกาศ State ดังนี้:

```kotlin
var uiState by mutableStateOf(InstallmentUiState())
    private set
```

`private set` ป้องกัน View แก้ State โดยตรง View ต้องส่ง Event ผ่าน `setPrice`, `setRate`, `setMonths` หรือ `calculate` เท่านั้น จึงรักษาทิศทางข้อมูลทางเดียว

## 17. การรับ Event จาก View

### `setPrice()`

รับเฉพาะตัวเลขและจุดทศนิยมด้วย Regular Expression `\d*\.?\d*` เมื่อราคาเปลี่ยนจะล้างผลเดิม เพราะผลเดิมไม่ตรงกับข้อมูลใหม่แล้ว

### `setRate()`

รับค่าจาก Slider อัตราดอกเบี้ย และล้างผลเดิม

### `setMonths()`

รับค่าจาก Slider จำนวนเดือน และใช้ `coerceIn(1, 36)` บังคับให้อยู่ในช่วง 1–36 เดือน

### `calculate()`

แปลงราคาเป็น Double ตรวจว่ามากกว่า 0 แล้วเรียก `InstallmentCalculator.calculate()` หากข้อมูลถูกต้องจะอัปเดต `uiState.result` และ Compose จะวาด Result Card อัตโนมัติ

## 18. การใช้ Lifecycle ViewModel Library

View เรียก ViewModel ด้วยฟังก์ชัน `viewModel()`

```kotlin
private fun InstallmentScreen(
    vm: InstallmentViewModel = viewModel()
)
```

ฟังก์ชันนี้ผูก ViewModel กับ `ViewModelStoreOwner` ของ Activity เมื่อหมุนจอ Activity เดิมถูกทำลาย แต่ ViewModel Instance เดิมยังอยู่ ทำให้ข้อมูลที่กรอก Slider และผลคำนวณไม่รีเซ็ต

## 19. สูตรดอกเบี้ยคงที่

กำหนดให้:

- `P` = ราคาสินค้าหรือเงินต้น
- `r` = อัตราดอกเบี้ยต่อเดือนเป็นเปอร์เซ็นต์
- `n` = จำนวนเดือน

สูตร:

```text
ดอกเบี้ยทั้งหมด = P × (r / 100) × n
ยอดรวมที่ต้องจ่าย = P + ดอกเบี้ยทั้งหมด
ค่างวดต่อเดือน = ยอดรวมที่ต้องจ่าย / n
```

โค้ดใน Model:

```kotlin
val totalInterest = price * (monthlyRatePercent / 100.0) * months
val totalPayment = price + totalInterest
val monthlyPayment = totalPayment / months
```

ตัวอย่าง ราคา 12,000 บาท ดอกเบี้ย 1% ต่อเดือน ระยะเวลา 12 เดือน:

```text
ดอกเบี้ย = 12,000 × 0.01 × 12 = 1,440 บาท
ยอดรวม = 12,000 + 1,440 = 13,440 บาท
ค่างวด = 13,440 / 12 = 1,120 บาทต่อเดือน
```

## 20. การแสดงผลลัพธ์

Result Card แสดงข้อมูลครบ ได้แก่

- ราคาสินค้า/เงินต้น
- อัตราดอกเบี้ยต่อเดือน
- ระยะเวลาผ่อน
- ค่างวดต่อเดือน
- ยอดรวมที่ต้องจ่ายจริง
- ยอดดอกเบี้ยทั้งหมด
- สูตรที่ใช้คำนวณ

ตัวเลขเงินใช้ `String.format(Locale.US, "%,.2f", value)` เพื่อใส่ comma และทศนิยม 2 ตำแหน่ง

---

## 21. AndroidManifest และ Gradle

### AndroidManifest

- `INTERNET` อนุญาตให้แผนที่ภายในโหลด Tile
- `<queries>` ทำให้แอปตรวจพบ Activity ที่รองรับ `geo:`
- `android:exported="true"` จำเป็นสำหรับ Launcher Activity ที่มี Intent Filter
- `windowSoftInputMode="adjustResize"` ปรับพื้นที่หน้าจอเมื่อ Keyboard ปรากฏ

### Gradle Dependencies ที่สำคัญ

- `androidx.activity.compose` เชื่อม Activity กับ Compose
- `androidx.compose.material3` ให้ UI Components เช่น Button, Card, Slider และ TextField
- `androidx.lifecycle.viewmodel.compose` ให้ฟังก์ชัน `viewModel()`
- `junit` ใช้รัน Unit Test

---

## 22. การทดสอบ

### Unit Test ของ Model

`InstallmentCalculatorTest` ส่งข้อมูล 12,000 บาท, 1%, 12 เดือน แล้วตรวจผลที่คาดหวังด้วย `assertEquals`

### Unit Test ของ ViewModel

`InstallmentViewModelTest` จำลอง Event เหมือนผู้ใช้กรอกและปรับ Slider จากนั้นเรียก `calculate()` และตรวจ `uiState.result`

### การทดสอบบน Emulator

ผลที่ตรวจสอบแล้ว:

- Build debug สำเร็จ
- Unit Test ผ่านทั้งหมด
- เปิดแอปและแสดงภาพจริงได้
- Logcat แสดง Lifecycle ครบตามลำดับ
- หมุนจอแล้ว Toast แสดง Lifecycle ใหม่ถูกต้อง
- พิกัดใน TextField ไม่หายหลังหมุนจอ
- แผนที่ภายในลาก ซูม และเลื่อนไปยัง Marker ใหม่ได้
- ปุ่มเปิดแผนที่ภายนอกเปิดแอปที่รองรับ URI แบบ `geo:` ได้
- ข้อมูลค่างวดและผลคำนวณไม่หายหลังหมุนจอ

---

## 23. ขั้นตอนสาธิตให้อาจารย์ดู

### สาธิตข้อ 1

1. เปิดแอปและชี้ให้เห็นภาพจริง ชื่อ และรหัสนักศึกษา
2. อธิบายว่า Image อ่านจาก Resource Drawable
3. หมุน Emulator จากแนวตั้งเป็นแนวนอนและกลับมา
4. ชี้ให้เห็น Toast และเปิด Logcat กรอง Tag `MidtermLifecycle`
5. เปิด GitHub และแสดง Source code บน Branch `main`

### สาธิตข้อ 2

1. เปิดแท็บ “แผนที่”
2. กดปุ่มขณะช่องว่าง เพื่อแสดง Toast ป้องกัน Crash
3. กรอก Latitude `17.1899` และ Longitude `104.0914`
4. กด “แสดงตำแหน่งบนแผนที่” และแสดง Marker ภายในแอป
5. ลากและซูมแผนที่ แล้วกดแสดงตำแหน่งอีกครั้งเพื่อเลื่อนกลับไปยัง Marker
6. หมุนหน้าจอและแสดงว่าพิกัดไม่หาย
7. กด “เปิดแอปแผนที่” เพื่อแสดง Implicit Intent ตามโจทย์

### สาธิตข้อ 3

1. เปิดแท็บ “ค่างวด”
2. กรอกราคา `12000`
3. ตั้งอัตราดอกเบี้ย `1.0%` และจำนวนเดือน `12`
4. กด “คำนวณค่างวด”
5. อธิบายผล 1,120 บาท/เดือน, ยอดรวม 13,440 บาท และดอกเบี้ย 1,440 บาท
6. หมุนหน้าจอและแสดงว่าข้อมูลและผลลัพธ์ยังอยู่

---

## 24. สคริปต์พรีเซนต์แบบย่อ

> สวัสดีค่ะ ดิฉันภัทรธิดา ดอกงาม รหัสนักศึกษา 67102122123 แอปนี้พัฒนาด้วย Kotlin และ Jetpack Compose แบ่งเป็นสามส่วนตามข้อสอบ คือ Activity Lifecycle, แผนที่แบบ State Hoisting และเครื่องคำนวณค่างวดแบบ MVVM
>
> ข้อแรก ดิฉันนำภาพจริงเข้า Resource Drawable และแสดงด้วย Image Composable ใน MainActivity มีการ Override Lifecycle ครบหกเมทอด ทุกเมทอดเรียกฟังก์ชันกลางเพื่อแสดง Toast โดยใช้ applicationContext และบันทึกลง Logcat เมื่อหมุนจอจะเห็นลำดับ onPause, onStop, onDestroy แล้วตามด้วย onCreate, onStart และ onResume ส่วน Source code ถูก Push ไว้บน GitHub Branch main แล้ว
>
> ข้อสอง ดิฉันใช้ MapScreen เป็น Stateful Composable ซึ่งเก็บ Latitude และ Longitude ด้วย rememberSaveable และใช้ CoordField เป็น Stateless Composable ที่รับ State และ Callback ทำให้ข้อมูลไหลทางเดียว เมื่อข้อมูลว่างหรือเกินช่วงจะแสดง Toast เมื่อข้อมูลถูกต้อง แผนที่ native จาก osmdroid จะเลื่อนไปยัง GeoPoint และแสดง Marker ส่วนปุ่มเปิดแอปแผนที่ใช้ ACTION_VIEW กับ URI แบบ geo:
>
> ข้อสาม ดิฉันแยก MVVM เป็นสามส่วน Model เก็บสูตรคำนวณ ViewModel เก็บ UI State และรับ Event ส่วน View มีหน้าที่แสดง UI เท่านั้น View เรียก ViewModel ด้วยฟังก์ชัน viewModel() จึงรักษาข้อมูลข้ามการหมุนจอได้ สูตรเป็นดอกเบี้ยคงที่ และมี Unit Test ตรวจทั้ง Calculator และ ViewModel ค่ะ

---

## 25. คำถามที่อาจารย์อาจถาม

### `remember` ต่างจาก `rememberSaveable` อย่างไร?

`remember` จำค่าได้เฉพาะระหว่าง Recomposition แต่ค่าจะหายเมื่อ Activity ถูกสร้างใหม่จากการหมุนจอ ส่วน `rememberSaveable` บันทึกค่าไว้ใน Saved State จึงรอดจาก Configuration Change

### Stateful กับ Stateless ต่างกันอย่างไร?

Stateful เป็นเจ้าของและเปลี่ยน State ส่วน Stateless ไม่เก็บ State แต่รับค่าผ่าน Parameter และแจ้ง Event ผ่าน Callback

### ทำไมต้องใช้ `toDoubleOrNull()`?

เพราะถ้าผู้ใช้กรอกข้อความที่แปลงเป็นเลขไม่ได้ ฟังก์ชันจะคืน `null` แทนการโยน Exception ทำให้เราตรวจและแสดง Toast ได้โดยแอปไม่ Crash

### Explicit Intent กับ Implicit Intent ต่างกันอย่างไร?

Explicit Intent ระบุ Component ปลายทางแน่นอน ส่วน Implicit Intent ระบุ Action และ Data แล้วให้ Android หาแอปที่รองรับ ในโครงงานใช้ `ACTION_VIEW` และ `geo:` จึงเป็น Implicit Intent

### ทำไมต้องมี `<queries>`?

ตั้งแต่ Android 11 ระบบจำกัด Package Visibility การประกาศ Intent Signature ใน `<queries>` ทำให้ `resolveActivity()` มองเห็นแอปแผนที่ที่รองรับ `geo:`

### ทำไม View ไม่คำนวณค่างวดเอง?

เพราะ MVVM กำหนดให้ View รับผิดชอบการแสดงผล ส่วน Business Logic อยู่ใน Model ทำให้แก้สูตรและเขียน Unit Test ได้โดยไม่ต้องเปิด Emulator

### ทำไมข้อมูลค่างวดไม่หายเมื่อหมุนจอ?

เพราะ State อยู่ใน `InstallmentViewModel` ที่ระบบเก็บใน ViewModelStore ข้ามการสร้าง Activity ใหม่ และหน้าจอเรียก Instance เดิมผ่าน `viewModel()`

### `private set` มีประโยชน์อย่างไร?

ทำให้ภายนอก ViewModel อ่าน `uiState` ได้ แต่แก้ค่าโดยตรงไม่ได้ ต้องส่ง Event ผ่านเมทอดที่กำหนด จึงควบคุม State และรักษา Unidirectional Data Flow

### ทำไมแผนที่ภายในยังมีปุ่มเปิดภายนอก?

แผนที่ภายในเป็นการปรับปรุง UX แต่ข้อสอบกำหนดให้สาธิต Implicit Intent ไปยังแอปแผนที่ภายนอก จึงเก็บทั้งสองรูปแบบเพื่อให้ใช้งานสะดวกและผ่านข้อกำหนดครบ

---

## 26. สรุป

แอปพลิเคชันนี้ทำตามข้อกำหนดครบทั้งสามส่วน ใช้ Compose สร้าง UI, ใช้ Lifecycle Callback และ Toast ถูกต้อง, ใช้ State Hoisting และ UDF กับฟอร์มพิกัด, ใช้ Implicit Intent แบบ `geo:`, แยก MVVM ชัดเจน และใช้ Lifecycle ViewModel รักษาข้อมูลข้ามการหมุนจอ โครงงานยังมี Unit Test, ประวัติ Git ที่ตรวจสอบได้ และแผนที่ภายในซึ่งช่วยให้ประสบการณ์ใช้งานสมบูรณ์ขึ้น

