package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val tagName = "MidtermLifecycle"
    override fun onCreate(state: Bundle?) { super.onCreate(state); notify("onCreate()"); enableEdgeToEdge(); setContent { MyApplicationTheme { ToolkitApp() } } }
    override fun onStart() { super.onStart(); notify("onStart()") }
    override fun onResume() { super.onResume(); notify("onResume()") }
    override fun onPause() { notify("onPause()"); super.onPause() }
    override fun onStop() { notify("onStop()"); super.onStop() }
    override fun onDestroy() { notify("onDestroy()"); super.onDestroy() }
    private fun notify(event: String) { Log.d(tagName, event); Toast.makeText(applicationContext, event, Toast.LENGTH_SHORT).show() }
}

@Composable
private fun ToolkitApp() {
    var page by rememberSaveable { mutableIntStateOf(0) }
    val nav = listOf("◉" to "โปรไฟล์", "⌖" to "แผนที่", "฿" to "ค่างวด")
    Scaffold(
        containerColor = Snow,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                nav.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = page == index, onClick = { page = index },
                        icon = { Text(item.first, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black) },
                        label = { Text(item.second) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Night, selectedTextColor = Night, indicatorColor = Mint,
                            unselectedIconColor = Muted, unselectedTextColor = Muted
                        )
                    )
                }
            }
        }
    ) { insets ->
        Box(Modifier.fillMaxSize().padding(insets)) {
            when (page) {
                0 -> ProfileScreen({ page = 1 }, { page = 2 })
                1 -> MapScreen()
                else -> InstallmentScreen()
            }
        }
    }
}

@Composable
private fun Hero(eyebrow: String, title: String, subtitle: String, symbol: String) {
    Box(
        Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Night, NightSoft)))
            .padding(horizontal = 22.dp, vertical = 24.dp)
    ) {
        Column(Modifier.padding(end = 68.dp)) {
            Text(eyebrow.uppercase(), color = Mint, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Text(subtitle, color = Color.White.copy(alpha = .65f), style = MaterialTheme.typography.bodyMedium)
        }
        Surface(
            Modifier.align(Alignment.TopEnd).size(54.dp), RoundedCornerShape(18.dp),
            Mint.copy(alpha = .14f), contentColor = Mint
        ) { Box(contentAlignment = Alignment.Center) { Text(symbol, style = MaterialTheme.typography.headlineMedium) } }
    }
}

@Composable
private fun ProfileScreen(openMap: () -> Unit, openCalculator: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box {
            Hero("Student toolkit", "สวัสดี, ภัทรธิดา", "พื้นที่รวมเครื่องมือสำหรับวิชา Android", "✦")
            Card(
                Modifier.padding(horizontal = 20.dp).padding(top = 150.dp),
                RoundedCornerShape(28.dp), CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painterResource(R.drawable.student_photo), "ภาพนักศึกษา", contentScale = ContentScale.Crop,
                        modifier = Modifier.size(82.dp).clip(RoundedCornerShape(22.dp)).border(3.dp, Mint, RoundedCornerShape(22.dp))
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("ภัทรธิดา ดอกงาม", style = MaterialTheme.typography.titleLarge)
                        Text("67102122123", color = Muted)
                        Spacer(Modifier.height(8.dp))
                        Pill("●  พร้อมใช้งาน")
                    }
                }
            }
        }
        Column(Modifier.padding(20.dp)) {
            SectionTitle("QUICK ACTIONS", "เลือกสิ่งที่ต้องการทำ")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard("⌖", "ค้นหาพิกัด", "ดูตำแหน่งบนแผนที่", Sky, Modifier.weight(1f), openMap)
                ActionCard("฿", "คำนวณผ่อน", "วางแผนยอดรายเดือน", Coral, Modifier.weight(1f), openCalculator)
            }
            Spacer(Modifier.height(24.dp))
            SectionTitle("ACTIVITY LIFECYCLE", "วงจรชีวิตของหน้าจอ")
            Spacer(Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Foreground journey", style = MaterialTheme.typography.titleMedium)
                    Text("หมุนจอหรือสลับแอป แล้วดู Toast และ Logcat", color = Muted)
                    Spacer(Modifier.height(18.dp))
                    val steps = listOf(
                        "01" to ("onCreate()" to "สร้าง Activity และ UI"),
                        "02" to ("onStart()" to "หน้าจอเริ่มมองเห็น"),
                        "03" to ("onResume()" to "พร้อมรับการโต้ตอบ"),
                        "04" to ("onPause()" to "เสียโฟกัสชั่วคราว"),
                        "05" to ("onStop()" to "หน้าจอถูกซ่อน"),
                        "06" to ("onDestroy()" to "Activity ถูกทำลาย")
                    )
                    steps.forEachIndexed { i, step -> LifecycleRow(step.first, step.second.first, step.second.second, i == steps.lastIndex) }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Pill(text: String) {
    Surface(color = MintSoft, shape = CircleShape) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Mint, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SectionTitle(label: String, title: String) {
    Column { Text(label, color = Muted, style = MaterialTheme.typography.labelMedium); Text(title, style = MaterialTheme.typography.titleLarge) }
}

@Composable
private fun ActionCard(symbol: String, title: String, detail: String, accent: Color, modifier: Modifier, click: () -> Unit) {
    Card(modifier.clickable(onClick = click), RoundedCornerShape(24.dp), CardDefaults.cardColors(NightSoft)) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = accent.copy(alpha = .18f)) {
                Text(symbol, Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = accent, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(24.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(detail, color = Color.White.copy(alpha = .55f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LifecycleRow(number: String, method: String, detail: String, last: Boolean) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(Modifier.size(38.dp), CircleShape, if (number == "03") Mint else Color(0xFFEEF2F6)) {
                Box(contentAlignment = Alignment.Center) { Text(number, fontWeight = FontWeight.Bold, color = Night) }
            }
            if (!last) Box(Modifier.width(2.dp).height(30.dp).background(Line))
        }
        Spacer(Modifier.width(13.dp))
        Column { Text(method, fontWeight = FontWeight.Bold); Text(detail, color = Muted, style = MaterialTheme.typography.bodyMedium) }
    }
}

@Composable
private fun MapScreen() {
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var shownLat by rememberSaveable { mutableStateOf("17.1899") }
    var shownLng by rememberSaveable { mutableStateOf("104.0914") }
    val context = LocalContext.current
    fun valid(): Pair<Double, Double>? {
        val lat = latitude.toDoubleOrNull()
        val lng = longitude.toDoubleOrNull()
        if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            Toast.makeText(context, "กรุณากรอกพิกัดให้ถูกต้อง", Toast.LENGTH_SHORT).show(); return null
        }
        return lat to lng
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Hero("Location explorer", "ค้นหาพิกัด", "ระบุ Latitude และ Longitude เพื่อปักหมุด", "⌖")
        Column(Modifier.padding(20.dp)) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Text("พิกัดปลายทาง", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CoordField(latitude, { latitude = it }, "Latitude", "17.1899", Modifier.weight(1f))
                        CoordField(longitude, { longitude = it }, "Longitude", "104.0914", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        { valid()?.let { shownLat = it.first.toString(); shownLng = it.second.toString() } },
                        Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                    ) { Text("แสดงตำแหน่ง") }
                    Text("Latitude −90 ถึง 90  •  Longitude −180 ถึง 180", Modifier.padding(top = 10.dp), color = Muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
                Column {
                    Box(Modifier.fillMaxWidth().height(330.dp).clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))) {
                        EmbeddedMap(shownLat, shownLng)
                        Surface(Modifier.align(Alignment.TopStart).padding(14.dp), CircleShape, Night.copy(alpha = .88f)) {
                            Text("●  LIVE MAP", Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = Mint, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("PINNED LOCATION", color = Muted, style = MaterialTheme.typography.labelMedium)
                            Text(shownLat + ", " + shownLng, style = MaterialTheme.typography.titleMedium)
                        }
                        FilledTonalButton(
                            onClick = {
                                val uri = "geo:0,0?q=" + shownLat + "," + shownLng
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                                else Toast.makeText(context, "ไม่พบแอปแผนที่", Toast.LENGTH_SHORT).show()
                            }, shape = RoundedCornerShape(14.dp)
                        ) { Text("เปิดแอป ↗") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordField(value: String, change: (String) -> Unit, label: String, hint: String, modifier: Modifier) {
    OutlinedTextField(
        value, change, modifier = modifier, label = { Text(label) }, placeholder = { Text(hint) },
        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun EmbeddedMap(latitude: String, longitude: String) {
    val lat = latitude.toDouble()
    val lng = longitude.toDouble()
    val point = GeoPoint(lat, lng)
    key(lat, lng) {
        Box(Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    Configuration.getInstance().userAgentValue =
                        "PhattharathidaStudentToolkit/1.0 (+https://github.com/phattharathidado67/67102122123)"
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        minZoomLevel = 3.0
                        maxZoomLevel = 20.0
                        controller.setZoom(16.0)
                        controller.setCenter(point)
                        overlays.add(Marker(this).apply {
                            position = point
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "ตำแหน่งที่เลือก"
                            snippet = String.format(Locale.US, "%.5f, %.5f", lat, lng)
                        })
                    }
                },
                onRelease = { it.onDetach() }
            )
            Text(
                "© OpenStreetMap contributors",
                Modifier.align(Alignment.BottomEnd).background(Color.White.copy(alpha = .82f)).padding(4.dp),
                color = Night, style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun InstallmentScreen(vm: InstallmentViewModel = viewModel()) {
    val state = vm.uiState
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Hero("Smart payment plan", "วางแผนค่างวด", "คำนวณยอดผ่อนด้วยอัตราดอกเบี้ยคงที่", "฿")
        Column(Modifier.padding(20.dp)) {
            state.result?.let { result ->
                Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(NightSoft)) {
                    Column(Modifier.fillMaxWidth().padding(22.dp)) {
                        Text("ยอดชำระต่อเดือน", color = Color.White.copy(alpha = .62f))
                        Text(money(result.monthlyPayment) + " ฿", color = Mint, style = MaterialTheme.typography.headlineLarge)
                        HorizontalDivider(Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = .12f))
                        Row {
                            DarkMetric("ยอดชำระรวม", money(result.totalPayment) + " ฿", Modifier.weight(1f))
                            DarkMetric("ดอกเบี้ยรวม", money(result.totalInterest) + " ฿", Modifier.weight(1f))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White)) {
                Column(Modifier.padding(18.dp)) {
                    Text("รายละเอียดการผ่อน", style = MaterialTheme.typography.titleLarge)
                    Text("ปรับตัวเลขเพื่อเปรียบเทียบแผนที่เหมาะกับคุณ", color = Muted)
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        state.price, vm::setPrice, modifier = Modifier.fillMaxWidth(), label = { Text("ราคาสินค้า") },
                        suffix = { Text("บาท") }, placeholder = { Text("เช่น 12000") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), shape = RoundedCornerShape(16.dp)
                    )
                    Spacer(Modifier.height(22.dp))
                    ValueHeader("ดอกเบี้ยต่อเดือน", oneDecimal(state.monthlyRate) + "%")
                    Slider(state.monthlyRate, vm::setRate, valueRange = 0f..5f, steps = 49)
                    Limits("0%", "5%")
                    Spacer(Modifier.height(18.dp))
                    ValueHeader("ระยะเวลาผ่อน", state.months.toString() + " เดือน")
                    Slider(state.months.toFloat(), { vm.setMonths(it.toInt()) }, valueRange = 1f..36f, steps = 34)
                    Limits("1 เดือน", "36 เดือน")
                    Spacer(Modifier.height(20.dp))
                    Button(
                        { if (!vm.calculate()) Toast.makeText(context, "กรุณากรอกราคาให้ถูกต้อง", Toast.LENGTH_SHORT).show() },
                        Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)
                    ) { Text("คำนวณแผนการผ่อน  →") }
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(color = Color(0xFFEAF4FF), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(16.dp)) {
                    Text("i", Modifier.size(28.dp).background(Sky, CircleShape).padding(top = 3.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(12.dp))
                    Text("สูตรดอกเบี้ยคงที่: เงินต้น × อัตราดอกเบี้ยต่อเดือน × จำนวนเดือน", color = Muted)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ValueHeader(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Surface(color = MintSoft, shape = CircleShape) { Text(value, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Mint, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun Limits(first: String, last: String) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text(first, color = Muted); Text(last, color = Muted) }
}

@Composable
private fun DarkMetric(label: String, value: String, modifier: Modifier) {
    Column(modifier) { Text(label, color = Color.White.copy(alpha = .55f)); Text(value, color = Color.White, fontWeight = FontWeight.Bold) }
}

private fun oneDecimal(value: Float) = String.format(Locale.US, "%.1f", value)
private fun money(value: Double) = String.format(Locale.US, "%,.2f", value)
